package service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.List;
import dao.AlimentoDAO;
import model.Alimento;

public class AlimentoService {

    private final AlimentoDAO alimentoDAO;
    private final Gson gson = new Gson();

    public AlimentoService(AlimentoDAO alimentoDAO) {
        this.alimentoDAO = alimentoDAO;
    }

    // Retorna todos os alimentos
    public Object getAll(Request request, Response response) {
        List<Alimento> alimentos = alimentoDAO.getAlimentos();
        response.type("application/json");
        response.status(200);
        return gson.toJson(alimentos);
    }

    // Retorna um alimento pelo ID
    public Object get(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            Alimento alimento = alimentoDAO.getPorId(id);

            if (alimento == null) {
                response.status(404);
                return gson.toJson("Alimento não encontrado");
            }

            response.type("application/json");
            response.status(200);
            return gson.toJson(alimento);
        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        }
    }

    // Cria um novo alimento
    public Object add(Request request, Response response) {
        try {
            Alimento alimento = gson.fromJson(request.body(), Alimento.class);
            validarAlimento(alimento);

            boolean ok = alimentoDAO.inserir(alimento);
            if (ok) {
                response.status(201);
                return gson.toJson("Alimento criado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao criar alimento. Verifique os dados.");
            }

        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao criar alimento: " + e.getMessage());
        }
    }

    // Atualiza um alimento existente
    public Object update(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));
            Alimento alimento = gson.fromJson(request.body(), Alimento.class);
            alimento.setId(id);

            if (alimentoDAO.getPorId(id) == null) {
                response.status(404);
                return gson.toJson("Alimento com ID " + id + " não encontrado.");
            }

            validarAlimento(alimento);
            boolean ok = alimentoDAO.atualizar(alimento);

            if (ok) {
                response.status(200);
                return gson.toJson("Alimento atualizado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao atualizar alimento. Verifique os dados.");
            }

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao atualizar alimento: " + e.getMessage());
        }
    }

    // Remove um alimento
    public Object remove(Request request, Response response) {
        try {
            int id = Integer.parseInt(request.params(":id"));

            if (alimentoDAO.getPorId(id) == null) {
                response.status(404);
                return gson.toJson("Alimento com ID " + id + " não encontrado para exclusão.");
            }

            boolean ok = alimentoDAO.excluir(id);
            if (ok) {
                response.status(200);
                return gson.toJson("Alimento excluído com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao excluir alimento. Verifique dependências.");
            }

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        }
    }

    // ----------------- Validação -----------------
    private void validarAlimento(Alimento alimento) {
        if (alimento == null) {
            throw new IllegalArgumentException("Dados do alimento não podem ser nulos.");
        }
        if (alimento.getNome() == null || alimento.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do alimento é obrigatório.");
        }
        if (alimento.getIdCategoria() == null || alimento.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("O ID da categoria deve ser válido e maior que zero.");
        }
    }
}
