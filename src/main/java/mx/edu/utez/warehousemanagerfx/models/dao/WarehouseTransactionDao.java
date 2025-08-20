package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Client;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.WarehouseTransaction;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarehouseTransactionDao {

    // --- CREATE ---
    public boolean createTransaction(WarehouseTransaction wt) {
        String sql = "INSERT INTO WAREHOUSE_TRANSACTION (Transaction_Type, Transaction_Date, Payment_Expiration_Date, Id_Warehouse, Id_Client, Id_Admin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, wt.getTransactionType());
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            if (wt.getPaymentExpirationDate() != null) {
                ps.setDate(3, Date.valueOf(wt.getPaymentExpirationDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, wt.getIdWarehouse());
            ps.setInt(5, wt.getIdClient());
            ps.setInt(6, wt.getIdAdmin());
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
                WarehouseTransaction wt = new WarehouseTransaction();

                wt.setIdTransaction(rs.getInt("Id_Transaction"));
                wt.setTransactionType(rs.getString("Transaction_Type"));
                wt.setTransactionDate(rs.getDate("Transaction_Date").toLocalDate());
                Date expDate = rs.getDate("Payment_Expiration_Date");
                wt.setPaymentExpirationDate(expDate != null ? expDate.toLocalDate() : null);

                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseCode(rs.getString("Warehouse_Code"));
                wt.setIdWarehouse(w.getIdWarehouse());

                Client c = new Client();
                c.setIdClient(rs.getInt("Id_Client"));
                c.setFirstName(rs.getString("Client_Name"));
                wt.setIdClient(c.getIdClient());

                Administrator a = new Administrator();
                a.setIdUser(rs.getInt("Id_Admin"));
                a.setFirstName(rs.getString("First_Name"));
                a.setMiddleName(rs.getString("Middle_Name"));
                a.setLastName(rs.getString("Last_Name"));
                a.setSecondLastName(rs.getString("Second_Last_Name"));
                wt.setIdAdmin(a.getIdUser());

                list.add(wt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- READ BY ID ---
    public WarehouseTransaction readTransactionById(int id) {
        String sql = "SELECT " +
                "    tx.Id_Transaction, " +
                "    tx.Transaction_Type, " +
                "    tx.Transaction_Date, " +
                "    tx.Payment_Expiration_Date, " +
                "    w.Id_Warehouse, " +
                "    c.Id_Client, " +
                "    adm.Id_Admin " +
                "FROM WAREHOUSE_TRANSACTION tx " +
                "JOIN WAREHOUSE w ON tx.Id_Warehouse = w.Id_Warehouse " +
                "JOIN CLIENT c ON tx.Id_Client = c.Id_Client " +
                "JOIN ADMINISTRATOR adm ON tx.Id_Admin = adm.Id_Admin " +
                "WHERE tx.Id_Client = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WarehouseTransaction wt = new WarehouseTransaction();

                wt.setIdTransaction(rs.getInt("Id_Transaction"));
                wt.setTransactionType(rs.getString("Transaction_Type"));
                wt.setTransactionDate(rs.getDate("Transaction_Date").toLocalDate());
                Date expDate = rs.getDate("Payment_Expiration_Date");
                wt.setPaymentExpirationDate(expDate != null ? expDate.toLocalDate() : null);

                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                wt.setIdWarehouse(w.getIdWarehouse());

                Client c = new Client();
                c.setIdClient(rs.getInt("Id_Client"));
                wt.setIdClient(c.getIdClient());

                Administrator a = new Administrator();
                a.setIdAdmin(rs.getInt("Id_Admin"));
                wt.setIdAdmin(a.getIdAdmin());

                return wt;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- UPDATE ---
    public boolean updateTransaction(WarehouseTransaction wt) {
        String sql = "UPDATE WAREHOUSE_TRANSACTION SET Transaction_Type = ?, Transaction_Date = ?, Payment_Expiration_Date = ?, Id_Warehouse = ?, Id_Client = ?, Id_Admin = ? " +
                "WHERE Id_Transaction = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, wt.getTransactionType());
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            if (wt.getPaymentExpirationDate() != null) {
                ps.setDate(3, Date.valueOf(wt.getPaymentExpirationDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, wt.getIdWarehouse());
            ps.setInt(5, wt.getIdClient());
            ps.setInt(6, wt.getIdAdmin());
            ps.setInt(7, wt.getIdTransaction());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
