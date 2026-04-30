package service;

import com.google.gson.*;
import model.ItemNotaOcr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Classe responsável por integração com Azure Document Intelligence para análise de notas fiscais
public class AzureOcrService {

    // Credenciais e configurações do serviço Azure
    private final String endpoint = "https://sistemainteligente-stockit.cognitiveservices.azure.com/";
    private final String key = "AE9yw7h1Wtzd5DQeZyC0mAdrunJEUwhHXJNPgyof7qCe7EWi9voEJQQJ99BKACBsN54XJ3w3AAALACOG9ezL";

    private final String apiVersion = "2023-07-31";

    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public AzureOcrService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // Analisa arquivo de nota fiscal enviando para modelo prebuilt-invoice do Azure
    // Retorna lista de itens extraídos com descrição e quantidade
    public List<ItemNotaOcr> analisarNota(byte[] arquivo, String contentType)
            throws IOException, InterruptedException {

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String modelId = "prebuilt-invoice";
        String url = String.format("%s/formrecognizer/documentModels/%s:analyze?api-version=%s",
                endpoint, modelId, apiVersion);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Ocp-Apim-Subscription-Key", key)
                .header("Content-Type", contentType)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(arquivo))
                .build();

        HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        int status = response.statusCode();

        if (status != 202) {
            throw new RuntimeException("Erro ao chamar Azure (status " + status + ")");
        }

        Optional<String> opLocation = response.headers().firstValue("operation-location");
        if (opLocation.isEmpty()) {
            throw new RuntimeException("Azure não retornou Operation-Location");
        }

        String operationLocation = opLocation.get();

        // Aguarda a conclusão do processamento assíncrono
        JsonObject analyzeResult = aguardarResultado(operationLocation);
        return extrairItens(analyzeResult);
    }

    // Executa polling na URL de operação até Azure concluir análise 
    private JsonObject aguardarResultado(String operationLocation)
            throws IOException, InterruptedException {

        while (true) {
            HttpRequest getReq = HttpRequest.newBuilder()
                    .uri(URI.create(operationLocation))
                    .header("Ocp-Apim-Subscription-Key", key)
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> getResp = httpClient.send(getReq, HttpResponse.BodyHandlers.ofString());
            int status = getResp.statusCode();

            if (status != 200) {
                throw new RuntimeException(
                        "Erro ao consultar resultado Azure (status " + status + "): " + getResp.body());
            }

            JsonObject root = gson.fromJson(getResp.body(), JsonObject.class);
            String s = root.get("status").getAsString();

            if ("succeeded".equalsIgnoreCase(s)) {
                return root.getAsJsonObject("analyzeResult");
            } else if ("failed".equalsIgnoreCase(s)) {
                throw new RuntimeException("Análise da nota falhou: " + getResp.body());
            }

            Thread.sleep(1000);
        }
    }

    // Tenta extrair itens em ordem de prioridade: invoice fields > tabelas > texto bruto
    // Retorna primeira estratégia bem-sucedida
    private List<ItemNotaOcr> extrairItens(JsonObject analyzeResult) {
        List<ItemNotaOcr> itens = new ArrayList<>();

        if (analyzeResult == null)
            return itens;

        // Estratégia 1: Campos estruturados de invoice (Items ou LineItems)
        List<ItemNotaOcr> viaInvoice = extrairItensViaInvoiceFields(analyzeResult);
        if (!viaInvoice.isEmpty()) {
            System.out.println("[Azure OCR] Itens encontrados via Invoice fields.");
            return viaInvoice;
        }

        // Estratégia 2: Extrai dados das tabelas reconhecidas
        List<ItemNotaOcr> viaTabelas = extrairItensViaTabelas(analyzeResult);
        if (!viaTabelas.isEmpty()) {
            System.out.println("[Azure OCR] Itens encontrados via Tabelas.");
            return viaTabelas;
        }

        // Estratégia 3: Fallback - processa texto completo linha por linha
        List<ItemNotaOcr> viaConteudo = extrairItensViaConteudo(analyzeResult);
        if (!viaConteudo.isEmpty()) {
            System.out.println("[Azure OCR] Itens encontrados via conteúdo de texto (fallback).");
        } else {
            System.out.println("[Azure OCR] Nenhum item encontrado em nenhum método.");
        }

        return viaConteudo;
    }

    // Fallback: quebra conteúdo completo em linhas e filtra possíveis itens
    // Remove linhas com palavras-chave de cabeçalho/rodapé
    private List<ItemNotaOcr> extrairItensViaConteudo(JsonObject analyzeResult) {
        List<ItemNotaOcr> itens = new ArrayList<>();

        if (!analyzeResult.has("content")) {
            return itens;
        }

        String content = analyzeResult.get("content").getAsString();
        if (content == null || content.isBlank()) {
            return itens;
        }

        String[] linhas = content.split("\\r?\\n");

        for (String linha : linhas) {
            if (linha == null)
                continue;
            String texto = linha.trim();
            if (texto.isBlank())
                continue;

            String lower = texto.toLowerCase();

            // Ignora linhas que parecem ser cabeçalho ou resumo financeiro
            if (lower.contains("produto") || lower.contains("descrição") || lower.contains("descricao")) {
                continue;
            }
            if (lower.contains("total") || lower.contains("valor") || lower.contains("nota fiscal")) {
                continue;
            }
            if (lower.startsWith("qtd") || lower.startsWith("quantidade")) {
                continue;
            }

            ItemNotaOcr item = new ItemNotaOcr();
            item.setDescricao(texto);
            item.setQuantidade(1); // Usa 1 como padrão quando quantidade não está disponível
            itens.add(item);
        }

        return itens;
    }

    // Extrai itens dos campos estruturados de invoice (Items ou LineItems do Azure)
    private List<ItemNotaOcr> extrairItensViaInvoiceFields(JsonObject analyzeResult) {
        List<ItemNotaOcr> itens = new ArrayList<>();

        var documents = analyzeResult.getAsJsonArray("documents");
        if (documents == null || documents.size() == 0)
            return itens;

        var doc = documents.get(0).getAsJsonObject();
        var fields = doc.getAsJsonObject("fields");
        if (fields == null)
            return itens;

        // Tenta encontrar campo Items ou LineItems (pode variar por tipo de nota)
        JsonObject itemsField = null;
        if (fields.has("Items") && fields.get("Items").isJsonObject()) {
            itemsField = fields.getAsJsonObject("Items");
        } else if (fields.has("LineItems") && fields.get("LineItems").isJsonObject()) {
            itemsField = fields.getAsJsonObject("LineItems");
        }

        if (itemsField == null || !itemsField.has("valueArray"))
            return itens;

        var valueArray = itemsField.getAsJsonArray("valueArray");

        for (var el : valueArray) {
            if (!el.isJsonObject())
                continue;

            var valueObj = el.getAsJsonObject().getAsJsonObject("valueObject");
            if (valueObj == null)
                continue;

            String descricao = getCampoString(valueObj, "Description");
            Double quantidadeNum = getCampoNumero(valueObj, "Quantity");
            if (quantidadeNum == null) {
                quantidadeNum = getCampoNumero(valueObj, "OrderQuantity");
            }

            int quantidade = (quantidadeNum == null || quantidadeNum <= 0)
                    ? 1
                    : quantidadeNum.intValue();

            ItemNotaOcr item = new ItemNotaOcr();
            item.setDescricao(descricao != null ? descricao : "Item sem descrição");
            item.setQuantidade(quantidade);

            itens.add(item);
        }

        return itens;
    }

    // Extrai itens das tabelas identificadas pelo Azure
    // Detecta coluna de descrição via heurística e processa dados a partir da linha 1 (cabeçalho na linha 0)
    private List<ItemNotaOcr> extrairItensViaTabelas(JsonObject analyzeResult) {
        List<ItemNotaOcr> itens = new ArrayList<>();

        var tables = analyzeResult.getAsJsonArray("tables");
        if (tables == null || tables.size() == 0) {
            return itens;
        }

        for (var t : tables) {
            if (!t.isJsonObject())
                continue;
            JsonObject table = t.getAsJsonObject();

            int rowCount = table.get("rowCount").getAsInt();
            int colCount = table.get("columnCount").getAsInt();
            var cells = table.getAsJsonArray("cells");
            if (cells == null)
                continue;

            // Identifica qual coluna contém as descrições dos produtos
            int colDescricao = encontrarColunaDescricao(cells, colCount);
            if (colDescricao < 0) {
                colDescricao = 0; // Fallback para primeira coluna
            }

            // Constrói matriz de células para acesso rápido
            String[][] matriz = new String[rowCount][colCount];
            for (var c : cells) {
                if (!c.isJsonObject())
                    continue;
                JsonObject cell = c.getAsJsonObject();
                int r = cell.get("rowIndex").getAsInt();
                int cIdx = cell.get("columnIndex").getAsInt();
                String content = cell.has("content") ? cell.get("content").getAsString() : "";
                matriz[r][cIdx] = content;
            }

            // Processa linhas de dados 
            for (int r = 1; r < rowCount; r++) {
                String desc = matriz[r][colDescricao];
                if (desc == null || desc.isBlank())
                    continue;

                ItemNotaOcr item = new ItemNotaOcr();
                item.setDescricao(desc.trim());
                item.setQuantidade(1);
                itens.add(item);
            }

            // Processa apenas primeira tabela com sucesso
            if (!itens.isEmpty())
                break;
        }

        return itens;
    }

    // Identifica a coluna de descrição através de scoring nos headers
    private int encontrarColunaDescricao(JsonArray cells, int colCount) {
        int[] scorePorColuna = new int[colCount];

        for (var c : cells) {
            if (!c.isJsonObject())
                continue;
            JsonObject cell = c.getAsJsonObject();
            int rowIndex = cell.get("rowIndex").getAsInt();
            if (rowIndex != 0)
                continue; // Analisa apenas linha de cabeçalho

            int colIndex = cell.get("columnIndex").getAsInt();
            String content = cell.has("content") ? cell.get("content").getAsString().toLowerCase() : "";

            // Scoring para detectar coluna de descrição
            if (content.contains("produto")) {
                scorePorColuna[colIndex] += 3;
            }
            if (content.contains("descr") || content.contains("descrição") || content.contains("descricao")) {
                scorePorColuna[colIndex] += 3;
            }
            if (content.contains("item")) {
                scorePorColuna[colIndex] += 1;
            }
        }

        // Retorna coluna com maior score
        int bestCol = -1;
        int bestScore = 0;
        for (int i = 0; i < colCount; i++) {
            if (scorePorColuna[i] > bestScore) {
                bestScore = scorePorColuna[i];
                bestCol = i;
            }
        }
        return bestCol;
    }

    // Extrai valor string de um campo estruturado 
    private String getCampoString(JsonObject obj, String nomeCampo) {
        if (!obj.has(nomeCampo) || !obj.get(nomeCampo).isJsonObject())
            return null;

        JsonObject campo = obj.getAsJsonObject(nomeCampo);
        if (campo.has("content") && campo.get("content").isJsonPrimitive()) {
            return campo.get("content").getAsString();
        }
        if (campo.has("valueString") && campo.get("valueString").isJsonPrimitive()) {
            return campo.get("valueString").getAsString();
        }
        return null;
    }

    // Extrai valor numérico de um campo estruturado 
    // Converte formato com vírgula para ponto decimal
    private Double getCampoNumero(JsonObject obj, String nomeCampo) {
        if (!obj.has(nomeCampo) || !obj.get(nomeCampo).isJsonObject())
            return null;

        JsonObject campo = obj.getAsJsonObject(nomeCampo);
        if (campo.has("valueNumber") && campo.get("valueNumber").isJsonPrimitive()) {
            try {
                return campo.get("valueNumber").getAsDouble();
            } catch (NumberFormatException ignored) {
            }
        }
        if (campo.has("content") && campo.get("content").isJsonPrimitive()) {
            try {
                return Double.parseDouble(campo.get("content").getAsString().replace(",", "."));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}