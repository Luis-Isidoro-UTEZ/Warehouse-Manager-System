package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.database.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Methods CRUD

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
        query.append(" ORDER BY ").append(orderColumn).append(" ").append(orderDir);

        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setIdWarehouse(rs.getInt("Id_Warehouse"));
                w.setWarehouseName(rs.getString("warehouseName"));
                w.setImage(rs.getString("Image"));
                w.setRentalPrice(rs.getDouble("Rental_Price"));
                w.setSalePrice(rs.getDouble("Sale_Price"));
                w.setSizeSqMeters(rs.getDouble("Size_Sq_Meters"));
                w.setStatus(rs.getString("Status"));
                warehouses.add(w);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read to get MIN/MAX
    /**
     * Method to obtain the minimum and maximum size/price of the warehouses table
     * @return array with [minimum, maximum]
     * @throws SQLException if the query fails
     */
    public double[] getMinMax(String columnName) {
        List<String> allowedColumns = List.of("Size_Sq_Meters", "Rental_Price", "Sale_Price");
        if (!allowedColumns.contains(columnName.toUpperCase())) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }
        double min = 0, max = 0;
        String query = "SELECT MIN(" + columnName + ") AS min_value, MAX(" + columnName + ") AS max_value FROM WAREHOUSE";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("min_value");
                max = rs.getDouble("max_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }
}
