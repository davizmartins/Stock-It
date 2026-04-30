package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ItemAmbiente;

public class ItemAmbienteDAO extends DAO {

    public ItemAmbienteDAO() {
        super();
    }

    // Listar todos os itens de ambiente
    public List<ItemAmbiente> listarTodos() {
        List<ItemAmbiente> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_ambiente";

        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ItemAmbiente item = new ItemAmbiente();
                item.setIdItemAmbiente(rs.getInt("id_item_ambiente"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setDataCadastro(rs.getDate("data_cadastro"));
                item.setDataVencimento(rs.getDate("data_vencimento"));
                item.setIdAlimento(rs.getInt("id_alimento"));
                item.setIdAmbiente(rs.getInt("id_ambiente"));
                itens.add(item);
            }
        } catch (SQLException e) {
        }
        return itens;
    }

    // Buscar item pelo ID
    public ItemAmbiente buscarPorId(int id) {
        ItemAmbiente item = null;
        String sql = "SELECT * FROM item_ambiente WHERE id_item_ambiente = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                item = new ItemAmbiente();
                item.setIdItemAmbiente(rs.getInt("id_item_ambiente"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setDataCadastro(rs.getDate("data_cadastro"));
                item.setDataVencimento(rs.getDate("data_vencimento"));
                item.setIdAlimento(rs.getInt("id_alimento"));
                item.setIdAmbiente(rs.getInt("id_ambiente"));
            }
        } catch (SQLException e) {
        }
        return item;
    }

    // Inserir um novo item
    public boolean inserir(ItemAmbiente item) {
        String sql = "INSERT INTO item_ambiente (quantidade, data_cadastro, data_vencimento, id_alimento, id_ambiente) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantidade());
            ps.setDate(2, item.getDataCadastro());
            ps.setDate(3, item.getDataVencimento());
            ps.setInt(4, item.getIdAlimento());
            ps.setInt(5, item.getIdAmbiente());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Atualizar um item existente
    public boolean atualizar(ItemAmbiente item) {
        String sql = "UPDATE item_ambiente SET quantidade = ?, data_cadastro = ?, data_vencimento = ?, id_alimento = ?, id_ambiente = ? WHERE id_item_ambiente = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantidade());
            ps.setDate(2, item.getDataCadastro());
            ps.setDate(3, item.getDataVencimento());
            ps.setInt(4, item.getIdAlimento());
            ps.setInt(5, item.getIdAmbiente());
            ps.setInt(6, item.getIdItemAmbiente());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Remover item por ID
    public boolean deletar(int id) {
        String sql = "DELETE FROM item_ambiente WHERE id_item_ambiente = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Remover todos os itens de um ambiente
    public boolean deletarPorAmbiente(int idAmbiente) {
        String sql = "DELETE FROM item_ambiente WHERE id_ambiente = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idAmbiente);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected >= 0; // return true even if zero rows (means none existed)
        } catch (SQLException e) {
            return false;
        }
    }
}
