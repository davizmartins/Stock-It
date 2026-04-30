package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Ambiente;

public class AmbienteDAO extends DAO {

    public AmbienteDAO() {
        super();
    }

    // CREATE
    public boolean inserir(Ambiente a) {
        boolean status = false;
        try {
            String sql = "INSERT INTO ambiente (nome_ambiente, tipo_ambiente) VALUES (?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, a.getNomeAmbiente());
            st.setString(2, String.valueOf(a.getTipoAmbiente()));
            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1)); // id = id_ambiente
            }

            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir ambiente: " + e.getMessage());
        }
        return status;
    }

    // READ - todos
    public List<Ambiente> getAmbientes() {
        List<Ambiente> ambientes = new ArrayList<>();
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ambiente ORDER BY id_ambiente");
            while (rs.next()) ambientes.add(mapear(rs));
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar ambientes: " + e.getMessage());
        }
        return ambientes;
    }

    // READ - por ID
    public Ambiente getPorId(int id) {
        Ambiente a = null;
        try {
            String sql = "SELECT * FROM ambiente WHERE id_ambiente = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) a = mapear(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ambiente por id: " + e.getMessage());
        }
        return a;
    }

    // UPDATE
    public boolean atualizar(Ambiente a) {
        boolean status = false;
        try {
            String sql = "UPDATE ambiente SET nome_ambiente = ?, tipo_ambiente = ? WHERE id_ambiente = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, a.getNomeAmbiente());
            st.setString(2, String.valueOf(a.getTipoAmbiente()));
            st.setInt(3, a.getId());
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar ambiente: " + e.getMessage());
        }
        return status;
    }

    // DELETE
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM ambiente WHERE id_ambiente = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            int rows = st.executeUpdate();
            st.close();
            status = rows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir ambiente: " + e.getMessage());
        }
        return status;
    }

    // Mapeia ResultSet -> Ambiente
    private Ambiente mapear(ResultSet rs) throws SQLException {
        String s = rs.getString("tipo_ambiente");
        char tipo = (s != null && !s.isEmpty()) ? s.charAt(0) : 'D'; // default seguro

        Ambiente a = new Ambiente();
        a.setId(rs.getInt("id_ambiente"));
        a.setNomeAmbiente(rs.getString("nome_ambiente"));
        a.setTipoAmbiente(tipo);
        return a;
    }
}
