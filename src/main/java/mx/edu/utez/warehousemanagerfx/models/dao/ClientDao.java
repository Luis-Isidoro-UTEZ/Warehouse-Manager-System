package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Client;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDao {

    // --- CREATE ---
    public boolean createClient(Client c) {
        String sql = "INSERT INTO CLIENT (First_Name, Middle_Name, Last_Name, Second_Last_Name, Email, Phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"Id_Client"})) {

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getMiddleName());
            ps.setString(3, c.getLastName());
            ps.setString(4, c.getSecondLastName());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getPhone());

            if (ps.executeUpdate() > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    c.setIdClient(keys.getInt(1));
                }
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- READ ALL ---
    public List<Client> readAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM CLIENT";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("Id_Client"),
                        rs.getString("First_Name"),
                        rs.getString("Middle_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Second_Last_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                );
                clients.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // --- READ BY ID ---
    public Optional<Client> findById(int idClient) {
        String sql = "SELECT Id_Client, Full_Name, Email, Phone FROM CLIENT WHERE Id_Client = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Client client = new Client(
                        rs.getInt("Id_Client"),
                        rs.getString("First_Name"),
                        rs.getString("Middle_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Second_Last_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                );
                rs.close();
                return Optional.of(client);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // --- UPDATE ---
    public boolean updateClient(Client client) {
        String sql = "UPDATE CLIENT SET Full_Name = ?, Email = ?, Phone = ? WHERE Id_Client = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getFirstName());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getPhone());
            ps.setInt(4, client.getIdClient());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- DELETE ---
    public boolean deleteClient(int idClient) {
        String sql = "DELETE FROM CLIENT WHERE Id_Client = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
