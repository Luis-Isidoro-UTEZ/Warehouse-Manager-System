package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserAccountDao {
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
