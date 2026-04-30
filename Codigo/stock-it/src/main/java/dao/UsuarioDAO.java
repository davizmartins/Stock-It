package dao;

import java.sql.*;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        super();
    }

    // CREATE - Cadastro de usuário
    public boolean inserir(Usuario u, String senhaPura) {
        boolean status = false;
        try {
            String senhaHash = BCrypt.hashpw(senhaPura, BCrypt.gensalt());
            String sql = "INSERT INTO usuario (nome, email, senha_hash) VALUES (?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, u.getNome());
            st.setString(2, u.getEmail());
            st.setString(3, senhaHash);

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next())
                    u.setId(rs.getInt(1));
            }

            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
        }
        return status;
    }

    // READ - Buscar por email
    public Usuario getPorEmail(String email) {
        Usuario u = null;
        try {
            String sql = "SELECT * FROM usuario WHERE email = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                u = mapear(rs);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
        }
        return u;
    }

    // LOGIN - Validar credenciais
    public boolean validarLogin(String email, String senhaPura) {
        Usuario u = getPorEmail(email);
        if (u != null) {
            return BCrypt.checkpw(senhaPura, u.getSenhaHash());
        }
        return false;
    }

    // UPDATE - Atualizar senha
    public boolean atualizarSenha(int id, String novaSenha) {
        boolean status = false;
        try {
            String senhaHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            String sql = "UPDATE usuario SET senha_hash = ? WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);

            st.setString(1, senhaHash);
            st.setInt(2, id);

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha: " + e.getMessage());
        }
        return status;
    }

    // DELETE - Excluir usuário
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM usuario WHERE id = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);

            int linhasAfetadas = st.executeUpdate();
            st.close();

            if (linhasAfetadas > 0) {
                status = true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
        }
        return status;
    }

    // MAP - Converter ResultSet em Usuario
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        return u;
    }
}
