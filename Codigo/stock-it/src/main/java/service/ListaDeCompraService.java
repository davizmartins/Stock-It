package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.List;
import dao.ListaDeCompraDAO;
import dao.ItemListaCompraDAO;
import model.ItemListaCompra;
import model.ListaDeCompra;

public class ListaDeCompraService {

    private final ListaDeCompraDAO listaDAO;
    private final ItemListaCompraDAO itemDAO;
    private final Gson gson = util.JsonUtil.GSON;

    public ListaDeCompraService(ListaDeCompraDAO listaDAO, ItemListaCompraDAO itemDAO) {
        this.listaDAO = listaDAO;
        this.itemDAO = itemDAO;
    }

    // Retorna todas as listas de compra
    public Object getAll(Request request, Response response) {
        List<ListaDeCompra> listas = listaDAO.getListas();
        response.type("application/json");
        return gson.toJson(listas);
    }

    // Retorna uma lista específica pelo ID
    public Object get(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        ListaDeCompra lista = listaDAO.getPorId(id);

        if (lista == null) {
            response.status(404);
            return gson.toJson("Lista não encontrada");
        }

        response.type("application/json");
        return gson.toJson(lista);
    }

    // Retorna uma lista com seus itens
    public Object getWithItems(Request request, Response response) {
        int idLista = Integer.parseInt(request.params(":id"));
        ListaDeCompra lista = listaDAO.getPorId(idLista);

        if (lista == null) {
            response.status(404);
            return gson.toJson("Lista não encontrada");
        }

        List<ItemListaCompra> itens = itemDAO.getPorLista(idLista);
        lista.setItens(itens);

        response.type("application/json");
        return gson.toJson(lista);
    }

    // Cria uma nova lista de compra
    public Object add(Request request, Response response) {
        ListaDeCompra lista = gson.fromJson(request.body(), ListaDeCompra.class);
        validarLista(lista);

        boolean ok = listaDAO.inserir(lista);
        if (ok) {
            response.status(201);
            return gson.toJson("Lista criada com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao criar lista");
        }
    }

    // Atualiza uma lista existente
    public Object update(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        ListaDeCompra lista = gson.fromJson(request.body(), ListaDeCompra.class);
        lista.setId(id);

        validarLista(lista);
        boolean ok = listaDAO.atualizar(lista);

        if (ok) {
            response.status(200);
            return gson.toJson("Lista atualizada com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao atualizar lista");
        }
    }

    // Exclui uma lista pelo ID
    public Object remove(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        boolean ok = listaDAO.excluir(id);

        if (ok) {
            response.status(200);
            return gson.toJson("Lista excluída com sucesso");
        } else {
            response.status(400);
            return gson.toJson("Erro ao excluir lista");
        }
    }

    // ---------------- VALIDAÇÃO ----------------
    private void validarLista(ListaDeCompra lista) {
        if (lista == null)
            throw new IllegalArgumentException("Lista não pode ser nula.");

        if (lista.getNome() == null || lista.getNome().trim().isEmpty())
            throw new IllegalArgumentException("Nome da lista é obrigatório.");
    }
}
