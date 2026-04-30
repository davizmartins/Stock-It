package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;
import dao.UsuarioDAO;
import model.Usuario;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final Gson gson = new Gson();

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // Cadastro de novo usuário
    public Object add(Request request, Response response) {
        if (!usuarioDAO.conectar()) {
            response.status(500);
            return gson.toJson("Falha ao conectar ao banco de dados.");
        }
        try {
            JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
            String nome = json.get("nome").getAsString();
            String email = json.get("email").getAsString();
            String senha = json.get("senha").getAsString();

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);

            validarUsuario(usuario, senha);

            boolean ok = usuarioDAO.inserir(usuario, senha);
            if (ok) {
                response.status(201);
                return gson.toJson("Usuário criado com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao criar usuário. Verifique os dados.");
            }

        } catch (IllegalArgumentException e) {
            response.status(400);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao criar usuário: " + e.getMessage());
        } finally {
            usuarioDAO.close();
        }
    }

    // Login do usuário
    public Object login(Request request, Response response) {
        if (!usuarioDAO.conectar()) {
            response.status(500);
            return gson.toJson("Falha ao conectar ao banco de dados.");
        }
        try {
            JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
            String email = json.get("email").getAsString();
            String senha = json.get("senha").getAsString();

            if (email == null || senha == null) {
                response.status(400);
                return gson.toJson("Email e senha são obrigatórios.");
            }

            boolean autenticado = usuarioDAO.validarLogin(email, senha);
            if (autenticado) {
                response.status(200);
                return gson.toJson("Login realizado com sucesso");
            } else {
                response.status(401);
                return gson.toJson("Credenciais inválidas");
            }

        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao realizar login: " + e.getMessage());
        } finally {
            usuarioDAO.close();
        }
    }

    // Atualizar senha
    public Object updatePassword(Request request, Response response) {
        if (!usuarioDAO.conectar()) {
            response.status(500);
            return gson.toJson("Falha ao conectar ao banco de dados.");
        }
        try {
            int id = Integer.parseInt(request.params(":id"));
            JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
            String novaSenha = json.get("novaSenha").getAsString();

            if (novaSenha == null || novaSenha.trim().isEmpty()) {
                response.status(400);
                return gson.toJson("Nova senha é obrigatória.");
            }

            boolean ok = usuarioDAO.atualizarSenha(id, novaSenha);
            if (ok) {
                response.status(200);
                return gson.toJson("Senha atualizada com sucesso");
            } else {
                response.status(400);
                return gson.toJson("Erro ao atualizar senha.");
            }

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao atualizar senha: " + e.getMessage());
        } finally {
            usuarioDAO.close();
        }
    }

    // Excluir usuário
    public Object remove(Request request, Response response) {
        if (!usuarioDAO.conectar()) {
            response.status(500);
            return gson.toJson("Falha ao conectar ao banco de dados.");
        }
        try {
            int id = Integer.parseInt(request.params(":id"));

            boolean ok = usuarioDAO.excluir(id);
            if (ok) {
                response.status(200);
                return gson.toJson("Usuário excluído com sucesso");
            } else {
                response.status(404);
                return gson.toJson("Usuário não encontrado para exclusão.");
            }

        } catch (NumberFormatException e) {
            response.status(400);
            return gson.toJson("ID inválido.");
        } catch (Exception e) {
            response.status(500);
            return gson.toJson("Erro no servidor ao excluir usuário: " + e.getMessage());
        } finally {
            usuarioDAO.close();
        }
    }

    // ----------------- Validação -----------------
    private void validarUsuario(Usuario usuario, String senha) {
        if (usuario == null) {
            throw new IllegalArgumentException("Dados do usuário não podem ser nulos.");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email do usuário é obrigatório.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
    }
}

