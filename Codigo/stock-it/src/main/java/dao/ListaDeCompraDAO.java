package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.ListaDeCompra;

public class ListaDeCompraDAO extends DAO {

    public ListaDeCompraDAO() {
        super();
    }

    // CREATE
    public boolean inserir(ListaDeCompra lista) {
        boolean status = false;
        try {
            String sql = "INSERT INTO lista_de_compra (nome_lista, data_compra) VALUES (?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, lista.getNome());
            if (lista.getDataCompra() != null)
                st.setDate(2, Date.valueOf(lista.getDataCompra()));
            else
                st.setNull(2, Types.DATE);

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir lista: " + e.getMessage());
        }
        return status;
    }

    // READ - buscar todas
    public List<ListaDeCompra> getListas() {
        List<ListaDeCompra> listas = new ArrayList<>();
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM lista_de_compra ORDER BY id_lista");
            while (rs.next()) {
                ListaDeCompra l = new ListaDeCompra(
                    rs.getInt("id_lista"),
                    rs.getString("nome_lista"),
                    rs.getDate("data_compra") != null ? rs.getDate("data_compra").toLocalDate() : null
                );
                listas.add(l);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar listas de compra: " + e.getMessage());
        }
        return listas;
    }

    // READ - buscar por ID
    public ListaDeCompra getPorId(int id) {
        ListaDeCompra lista = null;
        try {
            String sql = "SELECT * FROM lista_de_compra WHERE id_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                lista = new ListaDeCompra(
                    rs.getInt("id_lista"),
                    rs.getString("nome_lista"),
                    rs.getDate("data_compra") != null ? rs.getDate("data_compra").toLocalDate() : null
                );
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lista: " + e.getMessage());
        }
        return lista;
    }

        // UPDATE
    public boolean atualizar(ListaDeCompra lista) {
        boolean status = false;
        try {
            String sql = "UPDATE lista_de_compra SET nome_lista = ?, data_compra = ? WHERE id_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setString(1, lista.getNome());
            if (lista.getDataCompra() != null)
                st.setDate(2, Date.valueOf(lista.getDataCompra()));
            else
                st.setNull(2, Types.DATE);
            st.setInt(3, lista.getId());

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar lista: " + e.getMessage());
        }
        return status;
    }

    // DELETE
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM lista_de_compra WHERE id_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir lista: " + e.getMessage());
        }
        return status;
    }

}
