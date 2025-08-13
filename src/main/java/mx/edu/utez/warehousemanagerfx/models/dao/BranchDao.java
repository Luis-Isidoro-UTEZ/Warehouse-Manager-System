package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDao {

    // Methods CRUD

    // Read
    public List<Branch> readFilteredBranches(String orderColumn, String orderDir) {
        List<Branch> branches = new ArrayList<>();

        String col = orderColumn == null ? "ID_BRANCH" : orderColumn.toUpperCase();
        String dir = orderDir == null ? "ASC" : orderDir.toUpperCase();

        switch (col) {
            case "AVAILABLE_COUNT":
            case "RENTED_COUNT":
            case "SOLD_COUNT":
            case "ID_BRANCH":
                break;
            default:
                col = "ID_BRANCH";
        }
        if (!"ASC".equals(dir) && !"DESC".equals(dir)) {
            dir = "ASC";
        }

        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            String query =
                    "SELECT " +
                            "  b.ID_BRANCH                 AS ID_BRANCH, " +
                            "  b.BRANCH_CODE               AS BRANCH_CODE, " +
                            "  b.BRANCH_REGISTRATION_DATE  AS BRANCH_REGISTRATION_DATE, " +
                            "  (SELECT MIN(a.ID_USER) " +
                            "     FROM ADMINISTRATOR a " +
                            "    WHERE a.ID_BRANCH = b.ID_BRANCH AND a.IS_DELETED = 0) AS ID_ADMIN, " + // admin asignado (si existe)
                            "  p.ID_PROPERTY               AS ID_PROPERTY, " +
                            "  p.PROPERTY_TYPE             AS PROPERTY_TYPE, " +
                            "  p.COUNTRY                   AS COUNTRY, " +
                            "  p.STATE                     AS STATE, " +
                            "  p.MUNICIPALITY              AS MUNICIPALITY, " +
                            "  p.POSTAL_CODE               AS POSTAL_CODE, " +
                            "  p.NEIGHBORHOOD              AS NEIGHBORHOOD, " +
                            "  p.ADDRESS_DETAIL            AS ADDRESS_DETAIL, " +
                            "  (SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Available') AS AVAILABLE_COUNT, " +
                            "  (SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Rented')    AS RENTED_COUNT, " +
                            "  (SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Sold')      AS SOLD_COUNT " +
                            "FROM BRANCH b " +
                            "INNER JOIN PROPERTY p ON b.ID_PROPERTY = p.ID_PROPERTY " +
                            "ORDER BY " + col + " " + dir;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Branch b = new Branch();
                b.setIdBranch(rs.getInt("ID_BRANCH"));
                b.setBranchCode(rs.getString("BRANCH_CODE"));
                b.setRegistrationDate(rs.getDate("BRANCH_REGISTRATION_DATE").toLocalDate());

                // Set property data
                b.setIdProperty(rs.getInt("ID_PROPERTY"));
                b.setPropertyType(rs.getString("PROPERTY_TYPE"));
                b.setCountry(rs.getString("COUNTRY"));
                b.setState(rs.getString("STATE"));
                b.setMunicipality(rs.getString("MUNICIPALITY"));
                b.setPostalCode(rs.getString("POSTAL_CODE"));
                b.setNeighborhood(rs.getString("NEIGHBORHOOD"));
                b.setAddressDetail(rs.getString("ADDRESS_DETAIL"));

                // Set warehouse counts
                b.setAvailableCount(rs.getInt("AVAILABLE_COUNT"));
                b.setRentedCount(rs.getInt("RENTED_COUNT"));
                b.setSoldCount(rs.getInt("SOLD_COUNT"));

                int adminId = rs.getInt("ID_ADMIN");
                b.setIdAdmin(rs.wasNull() ? null : adminId);

                branches.add(b);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }
}
