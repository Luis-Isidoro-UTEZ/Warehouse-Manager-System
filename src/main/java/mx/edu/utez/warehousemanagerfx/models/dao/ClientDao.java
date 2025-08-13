package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Client;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDao {

    // --- CREATE ---
    public boolean createClient(Client client) {
        String sql = "INSERT INTO CLIENT (Full_Name, Email, Phone) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getFullName());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getPhone());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    client.setIdClient(keys.getInt(1));
                }
            }
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- READ ALL ---
    public List<Client> readAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT Id_Client, Full_Name, Email, Phone FROM CLIENT";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("Id_Client"),
                        rs.getString("Full_Name"),
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
                        rs.getString("Full_Name"),
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

            ps.setString(1, client.getFullName());
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
