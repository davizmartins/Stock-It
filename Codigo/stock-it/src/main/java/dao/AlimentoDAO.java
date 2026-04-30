package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Alimento;

public class AlimentoDAO extends DAO {

    public AlimentoDAO() {
        super();
    }

    // CREATE
    public boolean inserir(Alimento a) {
        boolean status = false;
        try {
            String sql = "INSERT INTO alimento (nome_alimento, id_categoria) VALUES (?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, a.getNome());
            st.setInt(2, a.getIdCategoria());

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }

            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir alimento: " + e.getMessage());
        }
        return status;
    }

    // READ - todos
    public List<Alimento> getAlimentos() {
        List<Alimento> alimentos = new ArrayList<>();
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM alimento ORDER BY id_alimento");

            while (rs.next()) {
                alimentos.add(mapear(rs));
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar alimentos: " + e.getMessage());
        }
        return alimentos;
    }

    // READ - por ID
    public Alimento getPorId(int id) {
        Alimento a = null;
        try {
            String sql = "SELECT * FROM alimento WHERE id_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                a = mapear(rs);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alimento por id: " + e.getMessage());
        }
        return a;
    }

    // UPDATE
    public boolean atualizar(Alimento a) {
        boolean status = false;
        try {
            String sql = "UPDATE alimento SET nome_alimento = ?, id_categoria = ? WHERE id_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);

            st.setString(1, a.getNome());
            st.setInt(2, a.getIdCategoria());
            st.setInt(3, a.getId());

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar alimento: " + e.getMessage());
        }
        return status;
    }

    // DELETE
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM alimento WHERE id_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);

            int linhasAfetadas = st.executeUpdate();
            st.close();

            if (linhasAfetadas > 0) {
                status = true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir alimento: " + e.getMessage());
        }
        return status;
    }

    // MAP
    private Alimento mapear(ResultSet rs) throws SQLException {
        Alimento a = new Alimento();
        a.setId(rs.getInt("id_alimento"));
        a.setNome(rs.getString("nome_alimento"));
        a.setIdCategoria(rs.getInt("id_categoria"));
        return a;
    }

    // Buscar alimento pelo nome (Função auxiliar do Sistema Inteligente)
    public Alimento buscarPorNome(String nome) {
        String sql = "SELECT * FROM alimento WHERE LOWER(nome_alimento) = LOWER(?) LIMIT 1";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alimento por nome: " + e.getMessage());
        }

        return null;
    }
}
