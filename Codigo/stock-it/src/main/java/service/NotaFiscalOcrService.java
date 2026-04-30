package service;

import com.google.gson.Gson;
import dao.AlimentoDAO;
import dao.ItemAmbienteDAO;
import model.Alimento;
import model.ItemAmbiente;
import model.ItemNotaOcr;
import spark.Request;
import spark.Response;
import util.JsonUtil;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

public class NotaFiscalOcrService {

    private final AzureOcrService azureOcrService;
    private final AlimentoDAO alimentoDAO;
    private final ItemAmbienteDAO itemAmbienteDAO;
    private final Gson gson = JsonUtil.GSON;

    // Categoria padrão para itens importados via OCR
    private static final int ID_CATEGORIA_PADRAO_OCR = 1;

    public NotaFiscalOcrService(AzureOcrService azureOcrService,
            AlimentoDAO alimentoDAO,
            ItemAmbienteDAO itemAmbienteDAO) {
        this.azureOcrService = azureOcrService;
        this.alimentoDAO = alimentoDAO;
        this.itemAmbienteDAO = itemAmbienteDAO;
    }

    public Object processarUploadNota(Request request, Response response) {
        try {
            byte[] arquivo = request.bodyAsBytes();
            if (arquivo == null || arquivo.length == 0) {
                response.status(400);
                return gson.toJson("Corpo da requisição está vazio. Envie o arquivo da nota como body.");
            }

            String contentType = request.contentType();

            //Extrai itens (nome + quantidade) usando o Azure
            List<ItemNotaOcr> itensOcr = azureOcrService.analisarNota(arquivo, contentType);

            //Retorna a lista de itens para o front
            response.type("application/json");
            response.status(200);
            return gson.toJson(itensOcr);

        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
            return gson.toJson("Erro ao processar nota: " + e.getMessage());
        }
    }

    public Object importarItensParaAmbiente(Request request, Response response) {
        try {
            String ambParam = request.queryParams("ambienteId");
            if (ambParam == null || ambParam.isBlank()) {
                response.status(400);
                return gson.toJson("Informe ambienteId na query string (?ambienteId=ID).");
            }

            int ambienteId = Integer.parseInt(ambParam);

            ItemNotaOcr[] itensArray = gson.fromJson(request.body(), ItemNotaOcr[].class);
            if (itensArray == null || itensArray.length == 0) {
                response.status(400);
                return gson.toJson("Nenhum item recebido para importação.");
            }

            salvarItensEmAmbiente(Arrays.asList(itensArray), ambienteId);

            response.status(200);
            response.type("application/json");
            return gson.toJson("Itens importados com sucesso para o ambiente " + ambienteId);

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ambienteId inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
            return gson.toJson("Erro ao importar itens: " + e.getMessage());
        }
    }

    // MÉTODOS PRIVADOS DE APOIO

    
    // Persiste a lista de itens OCR em um ambiente específico.
    private void salvarItensEmAmbiente(List<ItemNotaOcr> itensOcr, int ambienteId) {
        if (itensOcr == null || itensOcr.isEmpty())
            return;

        Date hoje = new Date(System.currentTimeMillis());

        for (ItemNotaOcr itemOcr : itensOcr) {
            try {
                if (itemOcr.getDescricao() == null || itemOcr.getDescricao().isBlank()) {
                    continue;
                }
                if (itemOcr.getQuantidade() == null || itemOcr.getQuantidade() <= 0) {
                    itemOcr.setQuantidade(1);
                }

                
                Alimento alimento = buscarOuCriarAlimento(itemOcr.getDescricao());

                // Converte data de vencimento (yyyy-MM-dd) recebida do front
                Date dataVenc = null;
                String strData = itemOcr.getDataVencimento();
                if (strData != null && !strData.isBlank()) {
                    try {
                        LocalDate ld = LocalDate.parse(strData); 
                        dataVenc = Date.valueOf(ld);
                    } catch (DateTimeParseException e) {
                        System.out.println("Data de vencimento inválida para '"
                                + itemOcr.getDescricao() + "': " + strData);
                    }
                }

                // Cria o registro em item_ambiente
                ItemAmbiente itemAmbiente = new ItemAmbiente();
                itemAmbiente.setQuantidade(itemOcr.getQuantidade());
                itemAmbiente.setDataCadastro(hoje);
                itemAmbiente.setDataVencimento(dataVenc); 
                itemAmbiente.setIdAlimento(alimento.getId());
                itemAmbiente.setIdAmbiente(ambienteId);

                itemAmbienteDAO.inserir(itemAmbiente);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Busca um alimento por nome; se não existir, cria um novo com a categoria padrão.
    private Alimento buscarOuCriarAlimento(String nomeAlimento) throws Exception {
        Alimento alimento = alimentoDAO.buscarPorNome(nomeAlimento);

        if (alimento != null) {
            return alimento;
        }

        alimento = new Alimento();
        alimento.setNome(nomeAlimento);
        alimento.setIdCategoria(ID_CATEGORIA_PADRAO_OCR);

        boolean ok = alimentoDAO.inserir(alimento);
        if (!ok) {
            throw new RuntimeException("Não foi possível inserir alimento: " + nomeAlimento);
        }

        // Recupera do banco para ter o ID preenchido
        alimento = alimentoDAO.buscarPorNome(nomeAlimento);
        if (alimento == null || alimento.getId() == null) {
            throw new RuntimeException("Alimento inserido mas ID não recuperado: " + nomeAlimento);
        }

        return alimento;
    }
}
