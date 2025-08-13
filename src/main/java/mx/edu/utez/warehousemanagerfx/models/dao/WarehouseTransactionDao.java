package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Client;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.WarehouseTransaction;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseTransactionDao {

    // --- CREATE ---
    public boolean createTransaction(WarehouseTransaction tx) {
        String sql = "INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getTransactionType());
            ps.setDate(2, Date.valueOf(tx.getTransactionDate()));
            if (tx.getPaymentExpirationDate() != null) {
                ps.setDate(3, Date.valueOf(tx.getPaymentExpirationDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, tx.getWarehouse().getIdWarehouse());
            ps.setInt(5, tx.getClient().getIdClient());
            ps.setInt(6, tx.getAdmin().getIdUser());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- READ ALL ---
    public List<WarehouseTransaction> readTransactions() {
        List<WarehouseTransaction> list = new ArrayList<>();
        String sql = "SELECT tx.Id_Transaction, tx.Transaction_Type, tx.Transaction_Date, tx.Payment_Expiration_Date, " +
                "w.Id_Warehouse, w.Warehouse_Code, " +
                "c.Id_Client, c.Full_Name AS Client_Name, " +
                "a.Id_User AS Admin_Id, a.Full_Name AS Admin_Name " +
                "FROM WAREHOUSE_TRANSACTION tx " +
                "JOIN WAREHOUSE w ON tx.Id_Warehouse = w.Id_Warehouse " +
                "JOIN CLIENT c ON tx.Id_Client = c.Id_Client " +
                "JOIN ADMINISTRATOR adm ON tx.Id_Admin = adm.Id_User " +
                "JOIN USER_ACCOUNT a ON adm.Id_User = a.Id_User";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                WarehouseTransaction tx = new WarehouseTransaction();

                tx.setIdTransaction(rs.getInt("Id_Transaction"));
                tx.setTransactionType(rs.getString("Transaction_Type"));
                tx.setTransactionDate(rs.getDate("Transaction_Date").toLocalDate());
                Date expDate = rs.getDate("Payment_Expiration_Date");
                tx.setPaymentExpirationDate(expDate != null ? expDate.toLocalDate() : null);

                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseCode(rs.getString("Warehouse_Code"));
                tx.setWarehouse(w);

                Client c = new Client();
                c.setIdClient(rs.getInt("Id_Client"));
                c.setFullName(rs.getString("Client_Name"));
                tx.setClient(c);

                Administrator adm = new Administrator();
                adm.setIdUser(rs.getInt("Admin_Id"));
                adm.setFullName(rs.getString("Admin_Name"));
                tx.setAdmin(adm);

                list.add(tx);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- READ BY ID ---
    public WarehouseTransaction readTransactionById(int id) {
        String sql = "SELECT tx.Id_Transaction, tx.Transaction_Type, tx.Transaction_Date, tx.Payment_Expiration_Date, " +
                "w.Id_Warehouse, w.Warehouse_Code, " +
                "c.Id_Client, c.Full_Name AS Client_Name, " +
                "a.Id_User AS Admin_Id, a.Full_Name AS Admin_Name " +
                "FROM WAREHOUSE_TRANSACTION tx " +
                "JOIN WAREHOUSE w ON tx.Id_Warehouse = w.Id_Warehouse " +
                "JOIN CLIENT c ON tx.Id_Client = c.Id_Client " +
                "JOIN ADMINISTRATOR adm ON tx.Id_Admin = adm.Id_User " +
                "JOIN USER_ACCOUNT a ON adm.Id_User = a.Id_User " +
                "WHERE tx.Id_Transaction = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WarehouseTransaction tx = new WarehouseTransaction();

                tx.setIdTransaction(rs.getInt("Id_Transaction"));
                tx.setTransactionType(rs.getString("Transaction_Type"));
                tx.setTransactionDate(rs.getDate("Transaction_Date").toLocalDate());
                Date expDate = rs.getDate("Payment_Expiration_Date");
                tx.setPaymentExpirationDate(expDate != null ? expDate.toLocalDate() : null);

                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseCode(rs.getString("Warehouse_Code"));
                tx.setWarehouse(w);

                Client c = new Client();
                c.setIdClient(rs.getInt("Id_Client"));
                c.setFullName(rs.getString("Client_Name"));
                tx.setClient(c);

                Administrator adm = new Administrator();
                adm.setIdUser(rs.getInt("Admin_Id"));
                adm.setFullName(rs.getString("Admin_Name"));
                tx.setAdmin(adm);

                return tx;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- DELETE (optional: soft delete if needed) ---
    public boolean deleteTransaction(int idTransaction) {
        String sql = "DELETE FROM WAREHOUSE_TRANSACTION WHERE Id_Transaction = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaction);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
