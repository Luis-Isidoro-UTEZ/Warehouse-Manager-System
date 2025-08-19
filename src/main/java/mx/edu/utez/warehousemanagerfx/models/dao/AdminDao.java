package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for an Admin model.
 * Joins USER_ACCOUNT (supertype) with ADMINISTRATOR (subtype).
 */
public class AdminDao {

    public class DuplicateCheckResult {
        private boolean emailExists;
        private boolean phoneExists;
        private boolean usernameExists;

        public DuplicateCheckResult(boolean emailExists, boolean phoneExists, boolean usernameExists) {
            this.emailExists = emailExists;
            this.phoneExists = phoneExists;
            this.usernameExists = usernameExists;
        }

        public boolean isEmailExists() { return emailExists; }
        public boolean isPhoneExists() { return phoneExists; }
        public boolean isUsernameExists() { return usernameExists; }

        public boolean anyDuplicate() {
            return emailExists || phoneExists || usernameExists;
        }
    }

    public DuplicateCheckResult checkDuplicate(String email, String phone, String username) {
        boolean emailExists = false;
        boolean phoneExists = false;
        boolean usernameExists = false;

        String sql = "SELECT EMAIL, PHONE, USERNAME FROM USER_ACCOUNT WHERE ROLE_TYPE = 'ADMINISTRATOR' AND (EMAIL = ? OR PHONE = ? OR USERNAME = ?)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (email.equalsIgnoreCase(rs.getString("EMAIL"))) emailExists = true;
                    if (phone.equals(rs.getString("PHONE"))) phoneExists = true;
                    if (username.equals(rs.getString("USERNAME"))) usernameExists = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DuplicateCheckResult(emailExists, phoneExists, usernameExists);
    }

    // CREATE: Insert new admin (USER_ACCOUNT + ADMINISTRATOR)
    public boolean create(Administrator admin) {
        String sqlUser = "INSERT INTO USER_ACCOUNT (First_Name, Middle_Name, Last_Name, Second_Last_Name, Email, Phone, Username, Password_Key, Role_Type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ADMINISTRATOR')";

        String sqlAdmin = "INSERT INTO ADMINISTRATOR (ID_ADMIN, Id_Branch) VALUES (?, ?)";

        try (Connection conn = DatabaseConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            // Insert into USER_ACCOUNT
            try (PreparedStatement psUser = conn.prepareStatement(sqlUser, new String[]{"ID_USER"})) {
                psUser.setString(1, admin.getFirstName());
                psUser.setString(2, admin.getMiddleName());
                psUser.setString(3, admin.getLastName());
                psUser.setString(4, admin.getSecondLastName());
                psUser.setString(5, admin.getEmail());
                psUser.setString(6, admin.getPhone());
                psUser.setString(7, admin.getUsername());
                psUser.setString(8, admin.getPasswordKey());
                psUser.executeUpdate();

                // Get generated Id_User
                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    admin.setIdUser(generatedId);
                    admin.setIdAdmin(generatedId);
                }
                rs.close();
            }

            // Insert into ADMINISTRATOR
            try (PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin)) {
                psAdmin.setInt(1, admin.getIdUser());
                if (admin.getIdBranch() != null) {
                    psAdmin.setInt(2, admin.getIdBranch());
                } else {
                    psAdmin.setNull(2, java.sql.Types.INTEGER);
                }
                psAdmin.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads admins applying ORDER BY.
     * Expected labels from UI:
     * - "Id_Admin" -> orders by u.Id_User
     * - "Name"     -> orders by u.Full_Name
     * - "Status"   -> orders by derived flag (Active=1 / Inactive=0) so:
     * DESC -> Active first, ASC -> Inactive first
     *
     * @param orderColumn UI-provided column label ("Id_Admin", "Name", "Status")
     * @param orderDir    "ASC" or "DESC"
     * @return list of Admin rows
     */
    public List<Administrator> readFilteredAdmins(String orderColumn, String orderDir) {
        List<Administrator> administrators = new ArrayList<>();

        String orderExpr;
        switch (orderColumn) {
            case "Id_Admin":
                orderExpr = "a.ID_ADMIN";
                break;
            case "Name":
                orderExpr = "u.FIRST_NAME";
                break;
            case "Status":
                orderExpr = "(CASE WHEN a.IS_DELETED = 0 THEN 1 ELSE 0 END)";
                break;
            default:
                orderExpr = "u.ID_USER";
        }
        String dir = "DESC".equalsIgnoreCase(orderDir) ? "DESC" : "ASC";

        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            String sql =
                    "SELECT " +
                            "  u.ID_USER             AS Id_User, " +
                            "  u.FIRST_NAME          AS First_Name, " +
                            "  u.MIDDLE_NAME         AS Middle_Name, " +
                            "  u.LAST_NAME           AS Last_Name, " +
                            "  u.SECOND_LAST_NAME    AS Second_Last_Name, " +
                            "  u.EMAIL               AS Email, " +
                            "  u.PHONE               AS Phone, " +
                            "  u.USERNAME            AS Username, " +
                            "  u.PASSWORD_KEY        AS Password_Key, " +
                            "  u.ROLE_TYPE           AS Role_Type, " +
                            "  a.ID_BRANCH           AS Id_Branch, " +
                            "  a.IS_DELETED          AS Is_Deleted " +
                            "FROM USER_ACCOUNT u " +
                            "JOIN ADMINISTRATOR a ON a.ID_ADMIN = u.ID_USER " +
                            "WHERE u.ROLE_TYPE = 'ADMINISTRATOR' " +
                            "ORDER BY " + orderExpr + " " + dir;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Administrator a = mapResultSetToAdmin(rs);
                administrators.add(a);
            }
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return administrators;
    }

    // READ: Get admin by ID
    public Administrator readById(int idUser) {
        String sql = "SELECT u.ID_USER AS Id_Admin, u.FULL_NAME AS Full_Name, u.EMAIL, u.PHONE, " +
                "u.USERNAME, u.PASSWORD_KEY, u.ROLE_TYPE, a.ID_BRANCH, a.IS_DELETED " +
                "FROM USER_ACCOUNT u " +
                "JOIN ADMINISTRATOR a ON a.ID_ADMIN = u.ID_USER " +
                "WHERE u.ID_USER = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE: Modify admin data
    public boolean update(Administrator admin) {
        String sqlUser = "UPDATE USER_ACCOUNT SET First_Name = ?, Middle_Name = ?, Last_Name = ?, Second_Last_Name = ?, Email = ?, Phone = ?, Username = ?, Password_Key = ? " +
                "WHERE Id_User = ?";

        String sqlAdmin = "UPDATE ADMINISTRATOR SET Id_Branch = ?, Is_Deleted = ? WHERE Id_Admin = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                psUser.setString(1, admin.getFirstName());
                psUser.setString(2, admin.getMiddleName());
                psUser.setString(3, admin.getLastName());
                psUser.setString(4, admin.getSecondLastName());
                psUser.setString(5, admin.getEmail());
                psUser.setString(6, admin.getPhone());
                psUser.setString(7, admin.getUsername());
                psUser.setString(8, admin.getPasswordKey());
                psUser.setInt(9, admin.getIdUser());
                psUser.executeUpdate();
            }

            try (PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin)) {
                if (admin.getIdBranch() != null) {
                    psAdmin.setInt(1, admin.getIdBranch());
                } else {
                    psAdmin.setNull(1, java.sql.Types.INTEGER);
                }
                psAdmin.setInt(2, admin.isDeleted() ? 1 : 0);
                psAdmin.setInt(3, admin.getIdUser());
                psAdmin.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE: Soft delete admin
    public boolean delete(int idAdmin) {
        String sql = "UPDATE ADMINISTRATOR SET Is_Deleted = 1 WHERE Id_Admin = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to map ResultSet to Administrator object
    private Administrator mapResultSetToAdmin(ResultSet rs) throws java.sql.SQLException {
        Administrator a = new Administrator();
        a.setIdUser(rs.getInt("ID_USER"));
        a.setIdAdmin(a.getIdUser());
        a.setFirstName(rs.getString("FIRST_NAME"));
        a.setMiddleName(rs.getString("MIDDLE_NAME"));
        a.setLastName(rs.getString("LAST_NAME"));
        a.setSecondLastName(rs.getString("SECOND_LAST_NAME"));
        a.setEmail(rs.getString("EMAIL"));
        a.setPhone(rs.getString("PHONE"));
        a.setUsername(rs.getString("USERNAME"));
        a.setPasswordKey(rs.getString("PASSWORD_KEY"));
        a.setRoleType(rs.getString("ROLE_TYPE"));
        int branchId = rs.getInt("ID_BRANCH");
        a.setIdBranch(rs.wasNull() ? null : branchId);
        a.setDeleted(rs.getInt("IS_DELETED") != 0);
        return a;
    }
}
