package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.ItemListaCompra;

public class ItemListaCompraDAO extends DAO {

    public ItemListaCompraDAO() {
        super();
    }

    // CREATE
    public boolean inserir(ItemListaCompra item) {
        boolean status = false;
        try {
            String sql = "INSERT INTO item_lista_compra (quantidade, status_compra, id_alimento, id_lista) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, item.getQuantidade());
            st.setString(2, String.valueOf(item.getStatus()));
            st.setInt(3, item.getIdAlimento());
            st.setInt(4, item.getIdLista());

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir item de lista: " + e.getMessage());
        }
        return status;
    }

    // READ - todos
    public List<ItemListaCompra> getItens() {
        List<ItemListaCompra> itens = new ArrayList<>();
        try {
            Statement st = conexao.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM item_lista_compra ORDER BY id_item_lista");
            while (rs.next()) {
                itens.add(mapear(rs));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens de lista: " + e.getMessage());
        }
        return itens;
    }

    // READ - por ID do Item
    public ItemListaCompra getPorId(int id) {
        ItemListaCompra item = null;
        try {
            String sql = "SELECT * FROM item_lista_compra WHERE id_item_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                item = mapear(rs);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao buscar item de lista: " + e.getMessage());
        }
        return item;
    }

    // READ - por ID da Lista
    public List<ItemListaCompra> getPorLista(int idLista) {
        List<ItemListaCompra> itens = new ArrayList<>();
        try {
            String sql = "SELECT * FROM item_lista_compra WHERE id_lista = ? ORDER BY id_item_lista";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, idLista);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                itens.add(mapear(rs));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens por lista: " + e.getMessage());
        }
        return itens;
    }

    // UPDATE
    public boolean atualizar(ItemListaCompra item) {
        boolean status = false;
        try {
            String sql = "UPDATE item_lista_compra " +
                    "SET quantidade = ?, status_compra = ?, id_alimento = ?, id_lista = ? " +
                    "WHERE id_item_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, item.getQuantidade());
            st.setString(2, String.valueOf(item.getStatus())); // char -> String(1)
            st.setInt(3, item.getIdAlimento());
            st.setInt(4, item.getIdLista());
            st.setInt(5, item.getId());

            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar item de lista: " + e.getMessage());
        }
        return status;
    }

    // DELETE
    public boolean excluir(int id) {
        boolean status = false;
        try {
            String sql = "DELETE FROM item_lista_compra WHERE id_item_lista = ?";
            PreparedStatement st = conexao.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            status = true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir item de lista: " + e.getMessage());
        }
        return status;
    }

    // Função reutilizável
    // Mapeia ResultSet para ItemLista
    private ItemListaCompra mapear(ResultSet rs) throws SQLException {
        String s = rs.getString("status_compra");
        // default 'P' se vier nulo
        char statusChar = (s != null && !s.isEmpty()) ? s.charAt(0) : 'P';

        ItemListaCompra item = new ItemListaCompra();
        item.setId(rs.getInt("id_item_lista"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setStatus(statusChar);
        item.setIdAlimento(rs.getInt("id_alimento"));
        item.setIdLista(rs.getInt("id_lista"));
        return item;
    }

}
