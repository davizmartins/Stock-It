package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

import dao.AmbienteDAO;
import dao.ItemAmbienteDAO;
import model.Ambiente;
import model.ItemAmbiente;

public class AmbienteService {

    private final AmbienteDAO ambienteDAO;
    private final ItemAmbienteDAO itemAmbienteDAO;
    private final Gson gson = new Gson();

    public AmbienteService(AmbienteDAO ambienteDAO, ItemAmbienteDAO itemAmbienteDAO) {
        this.ambienteDAO = ambienteDAO;
        this.itemAmbienteDAO = itemAmbienteDAO;
    }

    // Retorna todos os ambientes com seus itens
    public Object getAll(Request request, Response response) {
        List<Ambiente> ambientes = ambienteDAO.getAmbientes();

        List<Object> saida = new ArrayList<>();
        for (Ambiente a : ambientes) {
            saida.add(montarAmbienteComItens(a));
        }

        response.type("application/json");
        return gson.toJson(saida);
    }

    // Retorna um ambiente específico com seus itens
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        Ambiente ambiente = ambienteDAO.getPorId(id);

        if (ambiente == null) {
            response.status(404);
            return gson.toJson("Ambiente não encontrado");
        }

        response.type("application/json");
        return gson.toJson(montarAmbienteComItens(ambiente));
    }

    // Cria novo ambiente
    public Object add(Request request, Response response) {
        try {
            Ambiente ambiente = gson.fromJson(request.body(), Ambiente.class);
            validarAmbiente(ambiente);

            boolean ok = ambienteDAO.inserir(ambiente);
            if (ok) {
                response.status(201);
                java.util.Map<String, Object> out = new java.util.LinkedHashMap<>();
                out.put("id", ambiente.getId());
                out.put("message", "Ambiente criado com sucesso");
                return gson.toJson(out);
            } else {
                response.status(400);
                return gson.toJson("Erro ao criar ambiente");
            }

        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor: " + e.getMessage());
        }
    }

    // Atualiza dados do ambiente
    public Object update(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            Ambiente ambiente = gson.fromJson(request.body(), Ambiente.class);
            ambiente.setId(id);

            validarAmbiente(ambiente);
            boolean ok = ambienteDAO.atualizar(ambiente);

            if (ok) {
                response.status(200);
                return gson.toJson("Ambiente atualizado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao atualizar ambiente");
            }

        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor: " + e.getMessage());
        }
    }

    // Remove um ambiente do banco
    public Object remove(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            boolean ok = ambienteDAO.excluir(id);

            if (ok) {
                response.status(200);
                return gson.toJson("Ambiente excluído com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Não foi possível excluir o ambiente. Verifique se existem itens vinculados.");
            }
        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido");
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao tentar excluir ambiente: " + e.getMessage());
        }
    }

    // Valida os dados do ambiente antes de salvar
    private void validarAmbiente(Ambiente ambiente) {
        if (ambiente == null)
            throw new IllegalArgumentException("Ambiente não pode ser nulo.");

        if (ambiente.getNomeAmbiente() == null || ambiente.getNomeAmbiente().trim().isEmpty())
            throw new IllegalArgumentException("O nome do ambiente é obrigatório.");

        char tipo = Character.toUpperCase(ambiente.getTipoAmbiente());
        if (tipo != 'G' && tipo != 'F' && tipo != 'D')
            throw new IllegalArgumentException("Tipo de ambiente inválido. Use 'G', 'F' ou 'D'.");
        ambiente.setTipoAmbiente(tipo);
    }

    // Monta objeto com ambiente e lista de itens para retornar
    private Map<String, Object> montarAmbienteComItens(Ambiente a) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", a.getId());
        out.put("nome", a.getNomeAmbiente());
        out.put("tipo", mapTipo(a.getTipoAmbiente()));

        // Busca todos os itens e filtra pelos que pertencem a este ambiente
        List<Map<String, Object>> itens = new ArrayList<>();
        List<ItemAmbiente> todos = itemAmbienteDAO.listarTodos();

        for (ItemAmbiente it : todos) {
            if (it.getIdAmbiente() == a.getId()) {
                Map<String, Object> ii = new LinkedHashMap<>();
                ii.put("alimentoId", it.getIdAlimento());
                ii.put("quantidade", it.getQuantidade());
                ii.put("vencimento", it.getDataVencimento() != null ? it.getDataVencimento().toString() : null);
                ii.put("cadastro", it.getDataCadastro() != null ? it.getDataCadastro().toString() : null);
                itens.add(ii);
            }
        }

        out.put("itens", itens);
        return out;
    }

    // Converte o tipo de ambiente (char) para número
    private int mapTipo(char t) {
        t = Character.toUpperCase(t);
        if (t == 'G')
            return 1; // Geladeira
        if (t == 'F')
            return 2; // Freezer
        if (t == 'D')
            return 3; // Despensa
        return 0;
    }

    // Atualiza a lista de itens de um ambiente (remove antigos e insere novos)
    public Object patchItems(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            String body = request.body();
            java.util.Map<?, ?> map = gson.fromJson(body, java.util.Map.class);
            if (map == null || !map.containsKey("itens")) {
                response.status(400);
                return gson.toJson("Campo 'itens' ausente");
            }

            java.util.List<?> itens = (java.util.List<?>) map.get("itens");

            // Remove itens antigos do ambiente
            boolean deleted = itemAmbienteDAO.deletarPorAmbiente(id);
            if (!deleted) {
                response.status(500);
                return gson.toJson("Erro ao limpar itens existentes do ambiente");
            }

            // Função auxiliar para converter valores para inteiro
            java.util.function.Function<Object, Integer> toInt = (o) -> {
                if (o == null)
                    return 0;
                if (o instanceof Number)
                    return ((Number) o).intValue();
                try {
                    return Integer.parseInt(o.toString());
                } catch (Exception e) {
                    return 0;
                }
            };

            // Insere os novos itens
            for (Object obj : itens) {
                if (!(obj instanceof java.util.Map))
                    continue;
                java.util.Map itemMap = (java.util.Map) obj;

                ItemAmbiente it = new ItemAmbiente();
                it.setQuantidade(toInt.apply(itemMap.get("quantidade")));
                Object venc = itemMap.containsKey("vencimento") ? itemMap.get("vencimento")
                        : itemMap.get("dataVencimento");
                Object cad = itemMap.containsKey("cadastro") ? itemMap.get("cadastro") : itemMap.get("dataCadastro");
                it.setDataVencimento(venc);
                it.setDataCadastro(cad);
                Object aId = itemMap.containsKey("alimentoId") ? itemMap.get("alimentoId") : itemMap.get("idAlimento");
                it.setIdAlimento(toInt.apply(aId));
                it.setIdAmbiente(id);

                itemAmbienteDAO.inserir(it);
            }

            response.status(200);
            return gson.toJson("Itens atualizados com sucesso");

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
            return gson.toJson("Erro ao atualizar itens do ambiente: " + e.getMessage());
        }
    }
}