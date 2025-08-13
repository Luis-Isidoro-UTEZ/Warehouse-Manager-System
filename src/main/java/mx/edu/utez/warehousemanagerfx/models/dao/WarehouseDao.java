package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Methods CRUD

    // --- CREATE ---
    public boolean createWarehouse(Warehouse w) {
        String sql = "INSERT INTO WAREHOUSE (Warehouse_Code, Warehouse_Registration_Date, Warehouse_Name, Image, Rental_Price, Sale_Price, Size_Sq_Meters, Status, Id_Property, Id_Branch, Is_Deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getWarehouseCode());
            ps.setDate(2, Date.valueOf(w.getRegistrationDate()));
            ps.setString(3, w.getWarehouseName());
            ps.setString(4, w.getImage());
            ps.setDouble(5, w.getRentalPrice());
            ps.setDouble(6, w.getSalePrice());
            ps.setDouble(7, w.getSizeSqMeters());
            ps.setString(8, w.getStatus());
            ps.setInt(9, w.getBranch().getIdProperty()); // idProperty del branch
            ps.setInt(10, w.getBranch().getIdBranch());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- READ ALL / FILTERED ---
    public List<Warehouse> readWarehouses(int branchId) {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT w.*, " +
                "       b.Id_Branch, b.Branch_Code, b.Branch_Registration_Date, b.Is_Deleted AS Branch_Deleted, " +
                "       p.Id_Property, p.Property_Type, p.Country, p.State, p.Municipality, p.Postal_Code, p.Neighborhood, p.Address_Detail " +
                "FROM WAREHOUSE w " +
                "JOIN BRANCH b ON w.Id_Branch = b.Id_Branch " +
                "JOIN PROPERTY p ON w.Id_Property = p.Id_Property " +
                "WHERE w.Is_Deleted = 0 AND b.Id_Branch = ?";

        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseCode(rs.getString("Warehouse_Code"));
                w.setRegistrationDate(rs.getDate("Warehouse_Registration_Date").toLocalDate());
                w.setWarehouseName(rs.getString("Warehouse_Name"));
                w.setImage(rs.getString("Image"));
                w.setRentalPrice(rs.getDouble("Rental_Price"));
                w.setSalePrice(rs.getDouble("Sale_Price"));
                w.setSizeSqMeters(rs.getDouble("Size_Sq_Meters"));
                w.setStatus(rs.getString("Status"));
                w.setDeleted(rs.getInt("Is_Deleted") != 0);

                Branch b = new Branch();
                b.setIdBranch(rs.getInt("Id_Branch"));
                b.setBranchCode(rs.getString("Branch_Code"));
                b.setRegistrationDate(rs.getDate("Branch_Registration_Date").toLocalDate());
                b.setDeleted(rs.getInt("Branch_Deleted") != 0);

                // Property info
                b.setIdProperty(rs.getInt("Id_Property"));
                b.setPropertyType(rs.getString("Property_Type"));
                b.setCountry(rs.getString("Country"));
                b.setState(rs.getString("State"));
                b.setMunicipality(rs.getString("Municipality"));
                b.setPostalCode(rs.getInt("Postal_Code"));
                b.setNeighborhood(rs.getString("Neighborhood"));
                b.setAddressDetail(rs.getString("Address_Detail"));

                w.setBranch(b);
                list.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- READ BY ID ---
    public Warehouse readWarehouseById(int idWarehouse) {
        String sql = "SELECT w.*, " +
                "       b.Id_Branch, b.Branch_Code, b.Branch_Registration_Date, b.Is_Deleted AS Branch_Deleted, " +
                "       p.Id_Property, p.Property_Type, p.Country, p.State, p.Municipality, p.Postal_Code, p.Neighborhood, p.Address_Detail " +
                "FROM WAREHOUSE w " +
                "JOIN BRANCH b ON w.Id_Branch = b.Id_Branch " +
                "JOIN PROPERTY p ON w.Id_Property = p.Id_Property " +
                "WHERE w.Id_Warehouse = ? AND w.Is_Deleted = 0";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idWarehouse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseCode(rs.getString("Warehouse_Code"));
                w.setRegistrationDate(rs.getDate("Warehouse_Registration_Date").toLocalDate());
                w.setWarehouseName(rs.getString("Warehouse_Name"));
                w.setImage(rs.getString("Image"));
                w.setRentalPrice(rs.getDouble("Rental_Price"));
                w.setSalePrice(rs.getDouble("Sale_Price"));
                w.setSizeSqMeters(rs.getDouble("Size_Sq_Meters"));
                w.setStatus(rs.getString("Status"));
                w.setDeleted(rs.getInt("Is_Deleted") != 0);

                Branch b = new Branch();
                b.setIdBranch(rs.getInt("Id_Branch"));
                b.setBranchCode(rs.getString("Branch_Code"));
                b.setRegistrationDate(rs.getDate("Branch_Registration_Date").toLocalDate());
                b.setDeleted(rs.getInt("Branch_Deleted") != 0);

                b.setIdProperty(rs.getInt("Id_Property"));
                b.setPropertyType(rs.getString("Property_Type"));
                b.setCountry(rs.getString("Country"));
                b.setState(rs.getString("State"));
                b.setMunicipality(rs.getString("Municipality"));
                b.setPostalCode(rs.getInt("Postal_Code"));
                b.setNeighborhood(rs.getString("Neighborhood"));
                b.setAddressDetail(rs.getString("Address_Detail"));

                w.setBranch(b);
                return w;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- UPDATE ---
    public boolean updateWarehouse(Warehouse w) {
        String sql = "UPDATE WAREHOUSE SET Warehouse_Code=?, Warehouse_Registration_Date=?, Warehouse_Name=?, Image=?, Rental_Price=?, Sale_Price=?, Size_Sq_Meters=?, Status=?, Id_Property=?, Id_Branch=? WHERE Id_Warehouse=? AND Is_Deleted=0";
        try (Connection conn = DatabaseConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getWarehouseCode());
            ps.setDate(2, Date.valueOf(w.getRegistrationDate()));
            ps.setString(3, w.getWarehouseName());
            ps.setString(4, w.getImage());
            ps.setDouble(5, w.getRentalPrice());
            ps.setDouble(6, w.getSalePrice());
            ps.setDouble(7, w.getSizeSqMeters());
            ps.setString(8, w.getStatus());
            ps.setInt(9, w.getBranch().getIdProperty());
            ps.setInt(10, w.getBranch().getIdBranch());
            ps.setInt(11, w.getIdWarehouse());
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

    // Read
    /**
     * Flexible method to get warehouses according to a combination of filters:
     * @param keyword     keyword for multi-field search
     * @param status      status filter, "Show All/Status" to skip
     * @param minPrice    minimum price
     * @param maxPrice    maximum price
     * @param minSize     minimum size
     * @param maxSize     maximum size
     * @param priceRental true=filter Rental_Price; false=filter Sale_Price
     * @param orderColumn column for ORDER BY
     * @param orderDir    "ASC" o "DESC"
     * @param useKeyword      if it includes a keyword filter
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
        query.append(" AND ID_Branch = ?");
        params.add(1);
        query.append(" ORDER BY ").append(orderColumn).append(" ").append(orderDir);

        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseName(rs.getString("Warehouse_Name"));
                w.setImage(rs.getString("Image"));
                w.setRentalPrice(rs.getDouble("Rental_Price"));
                w.setSalePrice(rs.getDouble("Sale_Price"));
                w.setSizeSqMeters(rs.getDouble("Size_Sq_Meters"));
                w.setStatus(rs.getString("Status"));
                warehouses.add(w);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read to get MIN/MAX
    /**
     * Method to get the minimum and maximum size/price of the warehouses table
     * @return array with [minimum, maximum]
     * @throws SQLException if the query fails
     */
    public double[] getMinMax(String columnName) {
        List<String> allowedColumns = List.of("Size_Sq_Meters", "Rental_Price", "Sale_Price");
        if (!allowedColumns.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }
        double min = 0, max = 0;
        String query = "SELECT MIN(" + columnName + ") AS min_value, MAX(" + columnName + ") AS max_value FROM WAREHOUSE";
        try {
            Connection conn = DatabaseConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("min_value");
                max = rs.getDouble("max_value");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }
}
