package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.List;
import dao.CategoriaAlimentoDAO;
import model.CategoriaAlimento;

public class CategoriaAlimentoService {

    private final CategoriaAlimentoDAO categoriaAlimentoDAO;
    private final Gson gson = new Gson();

    public CategoriaAlimentoService(CategoriaAlimentoDAO categoriaAlimentoDAO) {
        this.categoriaAlimentoDAO = categoriaAlimentoDAO;
    }

    // Retorna todas as categorias
    public Object getAll(Request request, Response response) {
        List<CategoriaAlimento> categorias = categoriaAlimentoDAO.getCategorias();
        response.type("application/json");
        response.status(200);
        return gson.toJson(categorias);
    }

    // Retorna uma categoria pelo ID
    public Object get(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            CategoriaAlimento categoria = categoriaAlimentoDAO.getPorId(id);

            if (categoria == null) {
                response.status(404);
                return gson.toJson("Categoria de Alimento não encontrada");
            }

            response.type("application/json");
            response.status(200);
            return gson.toJson(categoria);
        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido no parâmetro da URL.");
        }
    }

    // Cria uma nova categoria
    public Object add(Request request, Response response) {
        try {
            CategoriaAlimento categoria = gson.fromJson(request.body(), CategoriaAlimento.class);
            
            validarCategoria(categoria);

            boolean ok = categoriaAlimentoDAO.inserir(categoria);
            if (ok) {
                response.status(201);
                return gson.toJson("Categoria de Alimento criada com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao criar Categoria de Alimento");
            }

        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor: " + e.getMessage());
        }
    }

    // Atualiza uma categoria existente
    public Object update(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            CategoriaAlimento categoria = gson.fromJson(request.body(), CategoriaAlimento.class);
            categoria.setId(id);

            if (categoriaAlimentoDAO.getPorId(id) == null) {
                response.status(404);
                return gson.toJson("Categoria com ID " + id + " não encontrada para atualização.");
            }
            
            validarCategoria(categoria);
            boolean ok = categoriaAlimentoDAO.atualizar(categoria);

            if (ok) {
                response.status(200);
                return gson.toJson("Categoria de Alimento atualizada com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao atualizar Categoria de Alimento");
            }

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido no parâmetro da URL.");
        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor: " + e.getMessage());
        }
    }

    // Exclui uma categoria
    public Object remove(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            
            if (categoriaAlimentoDAO.getPorId(id) == null) {
                response.status(404);
                return gson.toJson("Categoria com ID " + id + " não encontrada para exclusão.");
            }
            
            boolean ok = categoriaAlimentoDAO.excluir(id);

            if (ok) {
                response.status(200); // OK
                return gson.toJson("Categoria de Alimento excluída com sucesso");
            } else {
                response.status(400); 
                return gson.toJson("Erro ao excluir Categoria de Alimento. Verifique dependências.");
            }
            
        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        }
    }

    // ----------------- Validação -----------------
    private void validarCategoria(CategoriaAlimento categoria) {
        if (categoria == null)
            throw new IllegalArgumentException("Dados da categoria não podem ser nulos.");

        if (categoria.getNomeCategoria() == null || categoria.getNomeCategoria().trim().isEmpty())
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
            
        if (categoria.getNomeCategoria().trim().length() < 2)
            throw new IllegalArgumentException("O nome da categoria deve ter pelo menos 2 caracteres.");
    }
}