package mx.edu.utez.warehousemanagerfx.utils.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A factory class for providing database connections based on a configurable mode (LOCAL or CLOUD).
 * Use DatabaseConnectionFactory.getConnection() to get a connection.
 * OracleDatabaseConnectionManager.getConnectionLocal()/getConnectionCloud() everywhere.
 */
public final class DatabaseConnectionFactory {

    public enum Mode {
        LOCAL,
        CLOUD
    }

    // Default value: change here or at runtime using setMode(...)
    private static volatile Mode currentMode = Mode.LOCAL;

    private DatabaseConnectionFactory() {
        // utility class - not instantiable
    }

    public static Mode getMode() {
        return currentMode;
    }

    public static void setMode(Mode mode) {
        if (mode == null) throw new IllegalArgumentException("mode cannot be null");
        currentMode = mode;
    }

    /**
     * Returns a Connection according to the current mode.
     * Adjust the delegated calls if your OracleDatabaseConnectionManager has different method names.
     */
    public static Connection getConnection() throws SQLException {
        switch (currentMode) {
            case CLOUD:
                return OracleDatabaseConnectionManager.getConnection();
            case LOCAL:
            default:
                return OracleDatabaseConnectionManager.getConnectionLocal();
        }
    }
}