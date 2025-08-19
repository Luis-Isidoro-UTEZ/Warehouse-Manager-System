package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDao {

    // Methods CRUD

    // CREATE: Insert branch + property
    public boolean create(Branch branch) {
        String sqlProperty = "INSERT INTO PROPERTY (Property_Type, Country, State, Municipality, Postal_Code, Neighborhood, Address_Detail) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlBranch = "INSERT INTO BRANCH (Id_Property, Branch_Code, Branch_Registration_Date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            // Insert into PROPERTY
            int idProperty;
            try (PreparedStatement psProp = conn.prepareStatement(sqlProperty, new String[]{"ID_PROPERTY"})) {
                // If null, Storage Facility is assigned
                psProp.setString(1, branch.getPropertyType() != null ? branch.getPropertyType() : "Storage Facility");
                psProp.setString(2, branch.getCountry());
                psProp.setString(3, branch.getState());
                psProp.setString(4, branch.getMunicipality());
                psProp.setInt(5, branch.getPostalCode());
                psProp.setString(6, branch.getNeighborhood());
                psProp.setString(7, branch.getAddressDetail());
                psProp.executeUpdate();

                try (ResultSet rs = psProp.getGeneratedKeys()) {
                    if (rs.next()) {
                        idProperty = rs.getInt(1);
                        branch.setIdProperty(idProperty);
                    } else throw new SQLException("Failed to retrieve generated ID_PROPERTY");
                }
            }

            // Generate Branch_Code automatically
            String branchCode = "A" + idProperty + "-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) +
                    "-" + (int)(Math.random() * 9000 + 1000); // 4-digit random
            branch.setBranchCode(branchCode);

            // Automatic registration date
            branch.setRegistrationDate(java.time.LocalDate.now());

            // Insert into BRANCH
            try (PreparedStatement psBranch = conn.prepareStatement(sqlBranch, new String[]{"ID_BRANCH"})) {
                psBranch.setInt(1, branch.getIdProperty());
                psBranch.setString(2, branch.getBranchCode());
                psBranch.setDate(3, java.sql.Date.valueOf(branch.getRegistrationDate()));
                psBranch.executeUpdate();

                try (ResultSet rs = psBranch.getGeneratedKeys()) {
                    if (rs.next()) branch.setIdBranch(rs.getInt(1));
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ: Get all branches with optional order
    public List<Branch> readFilteredBranches(String orderColumn, String orderDir) {
        List<Branch> branches = new ArrayList<>();

        String col = orderColumn == null ? "b.ID_BRANCH" : orderColumn.toUpperCase();
        String dir = orderDir == null ? "ASC" : orderDir.toUpperCase();
        if (!"ASC".equals(dir) && !"DESC".equals(dir)) dir = "ASC";

        String query =
                "SELECT b.ID_BRANCH, b.BRANCH_CODE, b.BRANCH_REGISTRATION_DATE, b.IS_DELETED, " +
                        "p.ID_PROPERTY, p.PROPERTY_TYPE, p.COUNTRY, p.STATE, p.MUNICIPALITY, p.POSTAL_CODE, p.NEIGHBORHOOD, p.ADDRESS_DETAIL, " +
                        "(SELECT MIN(a.ID_ADMIN) FROM ADMINISTRATOR a WHERE a.ID_BRANCH = b.ID_BRANCH AND a.IS_DELETED = 0) AS ID_ADMIN, " +
                        "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Available') AS AVAILABLE_COUNT, " +
                        "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Rented') AS RENTED_COUNT, " +
                        "(SELECT COUNT(*) FROM WAREHOUSE w WHERE w.ID_BRANCH = b.ID_BRANCH AND w.STATUS = 'Sold') AS SOLD_COUNT " +
                        "FROM BRANCH b " +
                        "JOIN PROPERTY p ON b.ID_PROPERTY = p.ID_PROPERTY " +
                        "ORDER BY " + col + " " + dir;

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branches;
    }

    // READ BY ID
    public Branch readById(int idBranch) {
        String sql =
                "SELECT b.ID_BRANCH, b.BRANCH_CODE, b.BRANCH_REGISTRATION_DATE, b.IS_DELETED, " +
                        "p.ID_PROPERTY, p.PROPERTY_TYPE, p.COUNTRY, p.STATE, p.MUNICIPALITY, p.POSTAL_CODE, p.NEIGHBORHOOD, p.ADDRESS_DETAIL " +
                        "FROM BRANCH b " +
                        "JOIN PROPERTY p ON b.ID_PROPERTY = p.ID_PROPERTY " +
                        "WHERE b.ID_BRANCH = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBranch);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // UPDATE: Branch + property
    public boolean update(Branch branch) {
        // We only update the editable PROPERTY fields
        String sqlProperty = "UPDATE PROPERTY SET Property_Type=?, Country=?, State=?, Municipality=?, Postal_Code=?, Neighborhood=?, Address_Detail=? WHERE Id_Property=?";

        // We only update Is_Deleted in BRANCH
        String sqlBranch = "UPDATE BRANCH SET Is_Deleted=? WHERE Id_Branch=?";

        try (Connection conn = DatabaseConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            // Update PROPERTY
            try (PreparedStatement psProp = conn.prepareStatement(sqlProperty)) {
                psProp.setString(1, branch.getPropertyType() != null ? branch.getPropertyType() : "Storage Facility");
                psProp.setString(2, branch.getCountry());
                psProp.setString(3, branch.getState());
                psProp.setString(4, branch.getMunicipality());
                psProp.setInt(5, branch.getPostalCode());
                psProp.setString(6, branch.getNeighborhood());
                psProp.setString(7, branch.getAddressDetail());
                psProp.setInt(8, branch.getIdProperty());
                psProp.executeUpdate();
            }

            // Update only Is_Deleted in BRANCH
            try (PreparedStatement psBranch = conn.prepareStatement(sqlBranch)) {
                psBranch.setInt(1, branch.isDeleted() ? 1 : 0);
                psBranch.setInt(2, branch.getIdBranch());
                psBranch.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE: Soft delete branch
    public boolean delete(int idBranch) {
        String sql = "UPDATE BRANCH SET IS_DELETED = 1 WHERE ID_BRANCH = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBranch);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper mapping method
    private Branch mapResultSetToBranch(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setIdBranch(rs.getInt("ID_BRANCH"));
        b.setBranchCode(rs.getString("BRANCH_CODE"));
        b.setRegistrationDate(rs.getDate("BRANCH_REGISTRATION_DATE").toLocalDate());

        b.setIdProperty(rs.getInt("ID_PROPERTY"));
        b.setPropertyType(rs.getString("PROPERTY_TYPE"));
        b.setCountry(rs.getString("COUNTRY"));
        b.setState(rs.getString("STATE"));
        b.setMunicipality(rs.getString("MUNICIPALITY"));
        b.setPostalCode(rs.getInt("POSTAL_CODE"));
        b.setNeighborhood(rs.getString("NEIGHBORHOOD"));
        b.setAddressDetail(rs.getString("ADDRESS_DETAIL"));

        int adminId = rs.getInt("ID_ADMIN");
        b.setIdAdmin(rs.wasNull() ? null : adminId);

        b.setAvailableCount(rs.getInt("AVAILABLE_COUNT"));
        b.setRentedCount(rs.getInt("RENTED_COUNT"));
        b.setSoldCount(rs.getInt("SOLD_COUNT"));

        b.setDeleted(rs.getInt("IS_DELETED") != 0);

        return b;
    }
}
