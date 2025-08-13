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
                orderExpr = "u.ID_USER";
                break;
            case "Name":
                orderExpr = "u.FULL_NAME";
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
                            "  u.ID_USER       AS Id_Admin, " +
                            "  u.FULL_NAME     AS Full_Name, " +
                            "  u.EMAIL         AS Email, " +
                            "  u.PHONE         AS Phone, " +
                            "  u.USERNAME      AS Username, " +
                            "  u.PASSWORD_KEY  AS Password_Key, " +
                            "  u.ROLE_TYPE     AS Role_Type, " +
                            "  a.ID_BRANCH     AS Id_Branch, " +
                            "  a.IS_DELETED    AS Is_Deleted " +
                            "FROM USER_ACCOUNT u " +
                            "JOIN ADMINISTRATOR a ON a.ID_USER = u.ID_USER " +
                            "WHERE u.ROLE_TYPE = 'ADMINISTRATOR' " +
                            "ORDER BY " + orderExpr + " " + dir;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Administrator a = new Administrator();
                a.setIdUser(rs.getInt("Id_Admin"));
                a.setFullName(rs.getString("Full_Name"));
                a.setEmail(rs.getString("Email"));
                a.setPhone(rs.getString("Phone"));
                a.setUsername(rs.getString("Username"));
                a.setPasswordKey(rs.getString("Password_Key"));
                a.setRoleType(rs.getString("Role_Type"));
                int branchId = rs.getInt("Id_Branch");
                a.setIdBranch(rs.wasNull() ? null : branchId);
                a.setDeleted(rs.getInt("Is_Deleted") != 0);
                administrators.add(a);
            }
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return administrators;
    }
}
