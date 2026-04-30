package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.CategoriaAlimento;

public class CategoriaAlimentoDAO extends DAO {

    public CategoriaAlimentoDAO() {
        super();
    }
    
    // CREATE
    public boolean inserir(CategoriaAlimento c) {
        boolean status = false;
        try {

            String sql = "INSERT INTO categoria_alimento (nome_categoria) VALUES (?)";
            PreparedStatement st = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            st.setString(1, c.getNomeCategoria());

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }

            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir categoria: " + e.getMessage());
        }
        return status;
    }

    // READ - todos
    public List<CategoriaAlimento> getCategorias() {
        List<CategoriaAlimento> categorias = new ArrayList<>();
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria_alimento ORDER BY id_categoria_alimento");
            
            while (rs.next()) {
                categorias.add(mapear(rs));
            }
            
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }
        return categorias;
    }

    // READ - por ID
    public CategoriaAlimento getPorId(int id) {
        CategoriaAlimento c = null;
        try {
            String sql = "SELECT * FROM categoria_alimento WHERE id_categoria_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                c = mapear(rs);
            }
            
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria por id: " + e.getMessage());
        }
        return c;
    }

    // UPDATE
    public boolean atualizar(CategoriaAlimento c) {
        boolean status = false;
        try {
            String sql = "UPDATE categoria_alimento SET nome_categoria = ? WHERE id_categoria_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            
            st.setString(1, c.getNomeCategoria());
            st.setInt(2, c.getId());
            
            int linhasAfetadas = st.executeUpdate();
            st.close();
            
            if (linhasAfetadas > 0) {
                 status = true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
        }
        return status;
    }

    // DELETE
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM categoria_alimento WHERE id_categoria_alimento = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            
            int linhasAfetadas = st.executeUpdate();
            st.close();
            
            if (linhasAfetadas > 0) {
                 status = true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir categoria: " + e.getMessage());
        }
        return status;
    }

    //Map para as Categorias de Alimento
    private CategoriaAlimento mapear(ResultSet rs) throws SQLException {
        CategoriaAlimento c = new CategoriaAlimento();
        c.setId(rs.getInt("id_categoria_alimento"));
        c.setNomeCategoria(rs.getString("nome_categoria"));
        return c;
    }
}