package mx.edu.utez.warehousemanagerfx.utils.database;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleDatabaseConnectionManager {

    // CLOUD
    private static final String WALLET = "D:/Wallet_WarehouseManagement";
    private static final String DB_NAME = "warehousemanagement_high";
    private static final String DB_URL = "jdbc:oracle:thin:@" + DB_NAME + "?TNS_ADMIN=" + WALLET;
    private static final String DB_USER = "ADMIN";
    private static final String DB_PASSWORD = "UnrealEngine5!";
    private static final String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";

    // LOCAL
    private static final String hostname = "192.168.106.163";
    private static final String url = "jdbc:oracle:thin:@" + hostname + ":1521:XE";
    private static final String username = "C##EDGAR";
    private static final String password = "root";

    // Singleton: only one instance of the Pool
    private static PoolDataSource dataSource;

    static {
        try {
            dataSource = PoolDataSourceFactory.getPoolDataSource();
            dataSource.setConnectionFactoryClassName(CONN_FACTORY_CLASS_NAME);
            dataSource.setURL(DB_URL);
            dataSource.setUser(DB_USER);
            dataSource.setPassword(DB_PASSWORD);
            dataSource.setConnectionPoolName("JDBC_UCP_POOL");
            dataSource.setInitialPoolSize(5);
            dataSource.setMinPoolSize(5);
            dataSource.setMaxPoolSize(20);
            dataSource.setTimeoutCheckInterval(5);
            dataSource.setInactiveConnectionTimeout(10);

            Properties connProps = new Properties();
            connProps.setProperty("fixedString", "false");
            connProps.setProperty("remarksReporting", "false");
            connProps.setProperty("restrictGetTables", "false");
            connProps.setProperty("includeSynonyms", "false");
            connProps.setProperty("defaultNChar", "false");
            connProps.setProperty("AccumulateBatchResult", "false");

            dataSource.setConnectionProperties(connProps);

        } catch (Exception e) {
            System.err.println("Error initializing connection pool:");
            e.printStackTrace();
        }
    }

    // Public method to get the cloud connection
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("The connection pool was not initialized.");
        }
        return dataSource.getConnection();
    }

    // Public method to get the local connection
    public static Connection getConnectionLocal() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // public method to know that the connection was successful
    public static void main(String[] args){
        try (Connection conn = OracleDatabaseConnectionManager.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection to database successful.");
            } else {
                System.out.println("The connection is null or closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error trying to connect to the database:");
            e.printStackTrace();
        }
    }
}