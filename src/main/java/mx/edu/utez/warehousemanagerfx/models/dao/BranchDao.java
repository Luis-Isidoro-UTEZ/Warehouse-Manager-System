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
    public List<Branch> readBranches() {
        List<Branch> branches = new ArrayList<>();
        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            String query = "SELECT b.*, p.*, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Available') as available_count, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Rented') as rented_count, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Sold') as sold_count " +
                    "FROM BRANCH b " +
                    "INNER JOIN PROPERTY p ON b.ID_PROPERTY = p.ID_PROPERTY " +
                    "ORDER BY b.ID_BRANCH ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Branch b = new Branch();
                b.setIdBranch(rs.getInt("Id_Branch"));
                b.setBranchCode(rs.getString("Branch_Code"));
                b.setRegistrationDate(rs.getDate("Branch_Registration_Date").toLocalDate());

                // Set property data
                b.setIdProperty(rs.getInt("Id_Property"));
                b.setPropertyType(rs.getString("Property_Type"));
                b.setCountry(rs.getString("Country"));
                b.setState(rs.getString("State"));
                b.setMunicipality(rs.getString("Municipality"));
                b.setPostalCode(rs.getString("Postal_Code"));
                b.setNeighborhood(rs.getString("Neighborhood"));
                b.setAddressDetail(rs.getString("Address_Detail"));

                // Set warehouse counts
                b.setAvailableCount(rs.getInt("Available_Count"));
                b.setRentedCount(rs.getInt("Rented_Count"));
                b.setSoldCount(rs.getInt("Sold_Count"));

                branches.add(b);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    // Read
    public List<Branch> readFilteredBranches(String orderColumn, String orderDir) {
        List<Branch> branches = new ArrayList<>();
        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            String query = "SELECT b.*, p.*, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Available') as available_count, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Rented') as rented_count, " +
                    "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Sold') as sold_count " +
                    "FROM BRANCH b " +
                    "INNER JOIN PROPERTY p ON b.ID_PROPERTY = p.ID_PROPERTY " +
                    "ORDER BY " +
                    "CASE WHEN '" + orderColumn + "' IN ('Available_Count', 'Rented_Count', 'Sold_Count') " +
                    "     THEN (SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = CASE '" + orderColumn + "' " +
                    "          WHEN 'Available_Count' THEN 'Available' " +
                    "          WHEN 'Rented_Count' THEN 'Rented' " +
                    "          WHEN 'Sold_Count' THEN 'Sold' END) " +
                    "     ELSE b.ID_BRANCH END " + orderDir;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Branch b = new Branch();
                b.setIdBranch(rs.getInt("Id_Branch"));
                b.setBranchCode(rs.getString("Branch_Code"));
                b.setRegistrationDate(rs.getDate("Branch_Registration_Date").toLocalDate());

                // Set property data
                b.setIdProperty(rs.getInt("Id_Property"));
                b.setPropertyType(rs.getString("Property_Type"));
                b.setCountry(rs.getString("Country"));
                b.setState(rs.getString("State"));
                b.setMunicipality(rs.getString("Municipality"));
                b.setPostalCode(rs.getString("Postal_Code"));
                b.setNeighborhood(rs.getString("Neighborhood"));
                b.setAddressDetail(rs.getString("Address_Detail"));

                // Set warehouse counts
                b.setAvailableCount(rs.getInt("available_count"));
                b.setRentedCount(rs.getInt("rented_count"));
                b.setSoldCount(rs.getInt("sold_count"));

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
