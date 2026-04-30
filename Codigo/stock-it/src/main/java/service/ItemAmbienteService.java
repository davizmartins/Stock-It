package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.List;
import dao.ItemAmbienteDAO;
import model.ItemAmbiente;

public class ItemAmbienteService {

    private final ItemAmbienteDAO itemDAO;
    private final Gson gson = new Gson();

    public ItemAmbienteService(ItemAmbienteDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    // Retorna todos os itens de ambiente
    public Object getAll(Request request, Response response) {
        List<ItemAmbiente> itens = itemDAO.listarTodos();
        response.type("application/json");
        return gson.toJson(itens);
    }

    // Retorna um item específico pelo ID
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        ItemAmbiente item = itemDAO.buscarPorId(id);

        if (item == null) {
            response.status(404);
            return gson.toJson("Item não encontrado");
        }

        response.type("application/json");
        return gson.toJson(item);
    }

    // Adiciona um novo item
    public Object add(Request request, Response response) {
        try {
            ItemAmbiente item = gson.fromJson(request.body(), ItemAmbiente.class);
            validarItem(item);

            boolean ok = itemDAO.inserir(item);
            if (ok) {
                response.status(201);
                return gson.toJson("Item criado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao criar item - nenhuma linha foi inserida");
            }
        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro ao criar item: " + e.getMessage());
        }
    }

    // Atualiza um item existente
    public Object update(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            ItemAmbiente item = gson.fromJson(request.body(), ItemAmbiente.class);
            item.setIdItemAmbiente(id);

            validarItem(item);
            
            // Verificar se item existe antes de atualizar
            ItemAmbiente itemExistente = itemDAO.buscarPorId(id);
            if (itemExistente == null) {
                response.status(404);
                return gson.toJson("Item não encontrado");
            }
            
            boolean ok = itemDAO.atualizar(item);
            
            if (ok) {
                response.status(200);
                return gson.toJson("Item atualizado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao atualizar item - nenhuma linha foi alterada");
            }
        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro ao atualizar item: " + e.getMessage());
        }
    }

    // Remove um item
    public Object remove(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            
            // Verificar se item existe antes de deletar
            ItemAmbiente item = itemDAO.buscarPorId(id);
            if (item == null) {
                response.status(404);
                return gson.toJson("Item não encontrado");
            }
            
            boolean ok = itemDAO.deletar(id);
            if (ok) {
                response.status(200);
                return gson.toJson("Item removido com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao remover item");
            }
        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido");
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro ao remover item: " + e.getMessage());
        }
    }

    // Validação básica do item
    private void validarItem(ItemAmbiente item) {
        if (item == null)
            throw new IllegalArgumentException("Item não pode ser nulo.");

        if (item.getQuantidade() <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");

        if (item.getIdAlimento() <= 0)
            throw new IllegalArgumentException("É necessário informar um ID de alimento válido.");

        if (item.getIdAmbiente() <= 0)
            throw new IllegalArgumentException("É necessário informar um ID de ambiente válido.");
    }

}
