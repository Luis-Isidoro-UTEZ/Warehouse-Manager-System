package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.SuperAdministrator;
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
        String sql = "INSERT INTO USER_ACCOUNT (First_Name, Middle_Name, Last_Name, Second_Last_Name, Email, Phone, Username, Password_Key, Role_Type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getMiddleName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getSecondLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getUsername());
            ps.setString(8, user.getPasswordKey());
            ps.setString(9, user.getRoleType());

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
                        rs.getString("First_Name"),
                        rs.getString("Middle_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Second_Last_Name"),
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
                        rs.getString("First_Name"),
                        rs.getString("Middle_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Second_Last_Name"),
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
                        rs.getString("First_Name"),
                        rs.getString("Middle_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Second_Last_Name"),
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
        String sql = "UPDATE USER_ACCOUNT SET First_Name = ?, Middle_Name = ?, Last_Name = ?, Second_Last_Name = ?, Email = ?, Phone = ?, Username = ?, Password_Key = ?, Role_Type = ? " +
                "WHERE Id_User = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getMiddleName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getSecondLastName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getUsername());
            ps.setString(8, user.getPasswordKey());
            ps.setString(9, user.getRoleType());
            ps.setInt(10, user.getIdUser());

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
                SELECT Id_User, First_Name, Middle_Name, Last_Name, Second_Last_Name, Email, Phone, Username, Password_Key, Role_Type
                FROM USER_ACCOUNT
                WHERE (LOWER(Username) = LOWER(?) OR LOWER(Email) = LOWER(?))
                AND Password_Key = ?
                """;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            ps.setString(3, rawPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idUser = rs.getInt("Id_User");
                String firstName = rs.getString("First_Name");
                String middleName = rs.getString("Middle_Name");
                String lastName = rs.getString("Last_Name");
                String secondLastName = rs.getString("Second_Last_Name");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String username = rs.getString("Username");
                String passwordKey = rs.getString("Password_Key");
                String roleType = rs.getString("Role_Type");

                // Verify role
                if ("ADMINISTRATOR".equalsIgnoreCase(roleType)) {
                    final String sqlAdmin = """
                        SELECT Id_Branch, Is_Deleted
                        FROM ADMINISTRATOR
                        WHERE Id_Admin = ?
                        """;
                    try (PreparedStatement psAdmin = con.prepareStatement(sqlAdmin)) {
                        psAdmin.setInt(1, idUser);
                        try (ResultSet rsAdmin = psAdmin.executeQuery()) {
                            if (rsAdmin.next()) {
                                Administrator admin = new Administrator(
                                        idUser, firstName, middleName, lastName, secondLastName,
                                        email, phone, username, passwordKey, roleType,
                                        rsAdmin.getInt("Id_Branch"),
                                        rsAdmin.getBoolean("Is_Deleted")
                                );
                                return Optional.of(admin);
                            } else {
                                // No row exists in ADMINISTRATOR for this Id_Admin -> treat as error or empty
                                return Optional.empty();
                            }
                        }
                    }
                } else if ("SUPERADMINISTRATOR".equalsIgnoreCase(roleType)) {
                    SuperAdministrator superAdmin = new SuperAdministrator(
                            idUser, firstName, middleName, lastName, secondLastName,
                            email, phone, username, passwordKey, roleType
                    );
                    return Optional.of(superAdmin);
                }
            }
            rs.close();
            con.close();
        } catch (Exception e) {
            // Re-throw with the original exception so that LoginService logs the stacktrace
            throw new RuntimeException("Error in DAO findByIdentifierAndPassword. \n" +
                    "Error querying user.", e);
        }
        return Optional.empty();
    }
}
