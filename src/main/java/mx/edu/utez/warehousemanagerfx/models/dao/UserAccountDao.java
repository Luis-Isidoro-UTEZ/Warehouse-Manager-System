package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserAccountDao {
    // --- CREATE ---
    public boolean createUser(UserAccount user) {
        String sql = "INSERT INTO USER_ACCOUNT (Full_Name, Email, Phone, Username, Password_Key, Role_Type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPasswordKey());
            ps.setString(6, user.getRoleType());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- READ ALL ---
    public List<UserAccount> readAllUsers() {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type FROM USER_ACCOUNT";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserAccount user = new UserAccount(
                        rs.getInt("Id_User"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Username"),
                        rs.getString("Password_Key"),
                        rs.getString("Role_Type")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // --- READ BY ID ---
    public Optional<UserAccount> findById(int idUser) {
        String sql = "SELECT Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type " +
                "FROM USER_ACCOUNT WHERE Id_User = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserAccount user = new UserAccount(
                        rs.getInt("Id_User"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Username"),
                        rs.getString("Password_Key"),
                        rs.getString("Role_Type")
                );
                rs.close();
                return Optional.of(user);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // --- READ BY IDENTIFIER (USERNAME/EMAIL) AND PASSWORD ---
    public Optional<UserAccount> findByIdentifierAndPassword(String identifier, String rawPassword) {
        try (Connection con = DatabaseConnectionFactory.getConnection()) {
            final String sql = """
                SELECT Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type
                FROM USER_ACCOUNT
                WHERE LOWER(Username) = LOWER(?) OR LOWER(Email) = LOWER(?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Aquí debes validar la contraseña hash si tienes un sistema de hashing
                UserAccount user = new UserAccount(
                        rs.getInt("Id_User"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Username"),
                        rs.getString("Password_Key"),
                        rs.getString("Role_Type")
                );
                rs.close();
                return Optional.of(user);
            }
            rs.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al consultar usuario", e);
        }
        return Optional.empty();
    }

    // --- UPDATE ---
    public boolean updateUser(UserAccount user) {
        String sql = "UPDATE USER_ACCOUNT SET Full_Name = ?, Email = ?, Phone = ?, Username = ?, Password_Key = ?, Role_Type = ? " +
                "WHERE Id_User = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPasswordKey());
            ps.setString(6, user.getRoleType());
            ps.setInt(7, user.getIdUser());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- DELETE ---
    public boolean deleteUser(int idUser) {
        String sql = "DELETE FROM USER_ACCOUNT WHERE Id_User = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<UserAccount> findByIdentifierAndPassword(Connection con, String identifier, String rawPassword) {
        try {
            final String sql = """
                SELECT Id_User, Full_Name, Email, Phone, Username, Password_Key, Role_Type
                FROM USER_ACCOUNT
                WHERE (LOWER(Username) = LOWER(?) OR LOWER(Email) = LOWER(?))
                """;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserAccount user = new UserAccount(
                        rs.getInt("Id_User"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Username"),
                        rs.getString("Password_Key"),
                        rs.getString("Role_Type")
                );
                rs.close();
                con.close();
                return Optional.of(user);
            }
            rs.close();
            con.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar usuario", e);
        }
        return Optional.empty();
    }
}
