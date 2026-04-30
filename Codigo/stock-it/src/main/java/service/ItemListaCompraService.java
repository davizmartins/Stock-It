package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.List;
import dao.ItemListaCompraDAO;
import model.ItemListaCompra;

public class ItemListaCompraService {

    private final ItemListaCompraDAO itemDAO;
    private final Gson gson = new Gson();

    public ItemListaCompraService(ItemListaCompraDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    // Retorna todos os itens de todas as listas
    public Object getAll(Request request, Response response) {
        List<ItemListaCompra> itens = itemDAO.getItens();
        response.type("application/json");
        return gson.toJson(itens);
    }

    // Retorna todos os itens de uma lista específica (por query param ou rota)
    public Object getByLista(Request request, Response response) {
        int idLista = Integer.parseInt(request.params(":idLista"));
        List<ItemListaCompra> itens = itemDAO.getPorLista(idLista);
        response.type("application/json");
        return gson.toJson(itens);
    }

    // Retorna um item pelo ID
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        ItemListaCompra item = itemDAO.getPorId(id);

        if (item == null) {
            response.status(404);
            return gson.toJson("Item não encontrado");
        }

        response.type("application/json");
        return gson.toJson(item);
    }

    // Cria um novo item
    public Object add(Request request, Response response) {
        ItemListaCompra item = gson.fromJson(request.body(), ItemListaCompra.class);
        validarItem(item);

        boolean ok = itemDAO.inserir(item);
        if (ok) {
            response.status(201);
            return gson.toJson("Item criado com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao criar item");
        }
    }

    // Atualiza um item existente
    public Object update(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        ItemListaCompra item = gson.fromJson(request.body(), ItemListaCompra.class);
        item.setId(id);

        validarItem(item);
        boolean ok = itemDAO.atualizar(item);

        if (ok) {
            response.status(200);
            return gson.toJson("Item atualizado com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao atualizar item");
        }
    }

    // Exclui um item
    public Object remove(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        boolean ok = itemDAO.excluir(id);

        if (ok) {
            response.status(200);
            return gson.toJson("Item excluído com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao excluir item");
        }
    }

    // Atualiza apenas o status (P ou C)
    public Object updateStatus(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        String valor = request.queryParams("value");

        if (valor == null || valor.length() != 1) {
            response.status(400);
            return gson.toJson("Informe ?value=P ou ?value=C");
        }

        char novoStatus = Character.toUpperCase(valor.charAt(0));
        ItemListaCompra item = itemDAO.getPorId(id);
        if (item == null) {
            response.status(404);
            return gson.toJson("Item não encontrado");
        }

        item.setStatus(novoStatus);
        validarItem(item);

        boolean ok = itemDAO.atualizar(item);
        if (ok) {
            response.status(200);
            return gson.toJson("Status atualizado para " + novoStatus);
        } else {
            response.status(400);
            return gson.toJson("Erro ao atualizar status");
        }
    }

    // Valida os dados de um item antes de salvar
    private void validarItem(ItemListaCompra item) {
        if (item == null)
            throw new IllegalArgumentException("Item não pode ser nulo.");

        if (item.getQuantidade() == null || item.getQuantidade() <= 0)
            throw new IllegalArgumentException("Quantidade deve ser > 0.");

        if (item.getIdAlimento() == null)
            throw new IllegalArgumentException("idAlimento é obrigatório.");

        if (item.getIdLista() == null)
            throw new IllegalArgumentException("idLista é obrigatório.");

        // Valida o status ('P' ou 'C')
        char s = (item.getStatus() == 0) ? 'P' : Character.toUpperCase(item.getStatus());
        if (s != 'P' && s != 'C')
            throw new IllegalArgumentException("Status inválido. Use 'P' ou 'C'.");
        item.setStatus(s);
    }
}
