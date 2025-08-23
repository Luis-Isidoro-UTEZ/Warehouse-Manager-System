package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;
import mx.edu.utez.warehousemanagerfx.utils.services.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Methods CRUD

    // --- CREATE ---
    public boolean createWarehouse(Warehouse w) {
        Administrator admin = (Administrator) SessionManager.getCurrentUser();
        int branchId = admin.getIdBranch();

        try (Connection conn = DatabaseConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Transactional management

            // 1️⃣ Get the idProperty and branchCode of the branch
            String sqlBranch = "SELECT Id_Property, Branch_Code FROM BRANCH WHERE Id_Branch = ?";
            int idProperty;
            String branchCode;
            try (PreparedStatement psBranch = conn.prepareStatement(sqlBranch)) {
                psBranch.setInt(1, branchId);
                try (ResultSet rs = psBranch.executeQuery()) {
                    if (rs.next()) {
                        idProperty = rs.getInt("Id_Property");
                        branchCode = rs.getString("Branch_Code");
                    } else {
                        throw new SQLException("Branch not found with Id_Branch=" + branchId);
                    }
                }
            }

            // 2️⃣ Insert warehouse without warehouseCode, including idClient
            String sqlInsert = """
                        INSERT INTO WAREHOUSE
                        (Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Id_Property, Id_Branch, Id_Client, Is_Deleted)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                    """;
            int generatedIdWarehouse;
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert, new String[]{"Id_Warehouse"})) {
                // Copy image if path is selected
                if (w.getImage() != null && !w.getImage().isEmpty()) {
                    String fileName = copyImageToUploads(w.getImage());
                    w.setImage(fileName); // Save only the name in the DB
                }

                psInsert.setDate(1, Date.valueOf(LocalDate.now()));
                psInsert.setString(2, w.getWarehouseName());
                psInsert.setString(3, (w.getImage() == null || w.getImage().isEmpty()) ? "ImageNotAvailable.jpg" : w.getImage());
                psInsert.setDouble(4, w.getRentalPrice());
                psInsert.setDouble(5, w.getSalePrice());
                psInsert.setDouble(6, w.getSizeSqMeters());
                psInsert.setInt(7, idProperty);
                psInsert.setInt(8, branchId);

                // Handle Id_Client - can be null
                if (w.getIdClient() > 0) {
                    psInsert.setInt(9, w.getIdClient());
                } else {
                    psInsert.setNull(9, Types.INTEGER);
                }

                if (psInsert.executeUpdate() == 0) throw new SQLException("Creating warehouse failed.");

                try (ResultSet rsKey = psInsert.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        generatedIdWarehouse = rsKey.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve generated Id_Warehouse.");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 3️⃣ Generate warehouseCode and update record
            String warehouseCode = branchCode + "-" + generatedIdWarehouse;
            String sqlUpdate = "UPDATE WAREHOUSE SET Warehouse_Code = ? WHERE Id_Warehouse = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, warehouseCode);
                psUpdate.setInt(2, generatedIdWarehouse);
                psUpdate.executeUpdate();
            }

            conn.commit();
            w.setIdWarehouse(generatedIdWarehouse);
            w.setWarehouseCode(warehouseCode);
            w.setIdBranch(branchId);
            w.setIdProperty(idProperty);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String copyImageToUploads(String sourcePath) throws IOException {
        File source = new File(sourcePath);

        // "uploads" folder outside of resources (for writing)
        String targetDir = "uploads/thumbnails/";
        new File(targetDir).mkdirs(); // create if does not exist

        String fileName = System.currentTimeMillis() + "_" + source.getName();
        File target = new File(targetDir + fileName);

        Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Save only the name in the DB
    }

    // --- READ ---

    /**
     * Flexible method to get warehouses according to a combination of filters:
     *
     * @param keyword     keyword for multi-field search
     * @param status      status filter, "Show All/Status" to skip
     * @param minPrice    minimum price
     * @param maxPrice    maximum price
     * @param minSize     minimum size
     * @param maxSize     maximum size
     * @param priceRental true=filter Rental_Price; false=filter Sale_Price
     * @param orderColumn column for ORDER BY
     * @param orderDir    "ASC" o "DESC"
     * @param useKeyword  if it includes a keyword filter
     * @param useStatus   if it includes a status filter
     * @param useSlider   if it includes price/size filters
     */
    public List<Warehouse> readWarehouses(
            String keyword,
            String status,
            double minPrice,
            double maxPrice,
            double minSize,
            double maxSize,
            boolean priceRental,
            String orderColumn,
            String orderDir,
            boolean useKeyword,
            boolean useStatus,
            boolean useSlider
    ) {
        Administrator admin = (Administrator) SessionManager.getCurrentUser();
        int branchId = admin.getIdBranch();
        List<Warehouse> warehouses = new ArrayList<>();
        String priceCol = priceRental ? "Rental_Price" : "Sale_Price";

        StringBuilder query = new StringBuilder("SELECT * FROM WAREHOUSE WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (useKeyword) {
            query.append(" AND (Warehouse_Name LIKE ? OR Rental_Price LIKE ? OR Sale_Price LIKE ? OR Size_Sq_Meters LIKE ? OR Status LIKE ?)");
            String like = "%" + keyword + "%";
            for (int i = 0; i < 5; i++) params.add(like);
        }
        if (useStatus) {
            query.append(" AND Status = ?");
            params.add(status);
        }
        if (useSlider) {
            query.append(" AND ").append(priceCol).append(" BETWEEN ? AND ?");
            query.append(" AND Size_Sq_Meters BETWEEN ? AND ?");
            params.add(minPrice);
            params.add(maxPrice);
            params.add(minSize);
            params.add(maxSize);
        }
        query.append(" AND ID_Branch = ? AND Is_Deleted = 0");
        params.add(branchId);
        query.append(" ORDER BY ").append(orderColumn).append(" ").append(orderDir);

        try (
                Connection conn = DatabaseConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(query.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Warehouse w = new Warehouse();
                    w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                    w.setWarehouseCode(rs.getString("Warehouse_Code"));
                    // Convert SQL DATE → LocalDate
                    Date sqlDate = rs.getDate("Warehouse_Registration_Date");
                    if (sqlDate != null) {
                        w.setRegistrationDate(sqlDate.toLocalDate());
                    }
                    w.setWarehouseName(rs.getString("Warehouse_Name"));
                    w.setImage(rs.getString("Image"));
                    w.setRentalPrice(rs.getDouble("Rental_Price"));
                    w.setSalePrice(rs.getDouble("Sale_Price"));
                    w.setSizeSqMeters(rs.getDouble("Size_Sq_Meters"));
                    w.setStatus(rs.getString("Status"));
                    // Is_Deleted is NUMBER(1), we convert it to boolean
                    w.setDeleted(rs.getInt("Is_Deleted") == 1);
                    w.setIdProperty(rs.getInt("Id_Property"));
                    w.setIdBranch(rs.getInt("Id_Branch"));

                    // Handle Id_Client - can be null
                    int idClient = rs.getInt("Id_Client");
                    if (!rs.wasNull()) {
                        w.setIdClient(idClient);
                    } else {
                        w.setIdClient(0); // or whatever default value you prefer
                    }

                    warehouses.add(w);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // --- READ to get MIN/MAX ---

    /**
     * Method to get the minimum and maximum size/price of the warehouse table
     *
     * @return array with [minimum, maximum]
     * @throws SQLException if the query fails
     */
    public double[] getMinMax(String columnName) {
        List<String> allowedColumns = List.of("Size_Sq_Meters", "Rental_Price", "Sale_Price");
        if (!allowedColumns.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }
        double min = 0, max = 0;
        String query = "SELECT MIN(" + columnName + ") AS min_value, MAX(" + columnName + ") AS max_value FROM WAREHOUSE WHERE Is_Deleted = 0";
        try (
                Connection conn = DatabaseConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                min = rs.getDouble("min_value");
                max = rs.getDouble("max_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }

    // --- UPDATE ---
    public boolean updateWarehouse(Warehouse w) {
        String sql = "UPDATE WAREHOUSE SET Warehouse_Name=?, Image=?, Rental_Price=?, Sale_Price=?, Size_Sq_Meters=?, Status=?, Id_Client=? WHERE Id_Warehouse=? AND Is_Deleted=0";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getWarehouseName());
            ps.setString(2, w.getImage());
            ps.setDouble(3, w.getRentalPrice());
            ps.setDouble(4, w.getSalePrice());
            ps.setDouble(5, w.getSizeSqMeters());
            ps.setString(6, w.getStatus());

            // Handle Id_Client - can be null
            if (w.getIdClient() > 0) {
                ps.setInt(7, w.getIdClient());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setInt(8, w.getIdWarehouse());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- UPDATE CLIENT ASSIGNMENT ---

    /**
     * Method to assign or unassign a client to a warehouse
     *
     * @param idWarehouse warehouse ID
     * @param idClient    client ID (0 or null to unassign)
     * @param newStatus   new status for the warehouse
     * @return true if successful
     */
    public boolean assignClientToWarehouse(int idWarehouse, Integer idClient, String newStatus) {
        String sql = "UPDATE WAREHOUSE SET Id_Client=?, Status=? WHERE Id_Warehouse=? AND Is_Deleted=0";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (idClient != null && idClient > 0) {
                ps.setInt(1, idClient);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, newStatus);
            ps.setInt(3, idWarehouse);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- DELETE ---
    public boolean deleteWarehouse(int idWarehouse) {
        String sql = "DELETE FROM WAREHOUSE WHERE Id_Warehouse = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idWarehouse);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- SOFT DELETE ---
    public boolean softDeleteWarehouse(int idWarehouse) {
        String sql = "UPDATE WAREHOUSE SET Is_Deleted = 1 WHERE Id_Warehouse = ?";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idWarehouse);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}