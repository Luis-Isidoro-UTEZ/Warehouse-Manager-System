package mx.edu.utez.warehousemanagerfx.utils.services;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.models.dao.UserAccountDao;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * LoginService mejorado:
 * - authenticate(identifier, password, listener) decide si intentar cloud según hasInternetFast()
 * - notifica eventos 'before' (solo cuando realmente va a intentar), 'update' (mensajes libres) y 'after' (AttemptResult)
 */
public class LoginService {

    public enum ErrorType {
        NONE,
        CLOUD_NO_INTERNET,
        CLOUD_UNAVAILABLE,
        LOCAL_UNAVAILABLE,
        BOTH_UNAVAILABLE,
        INVALID_CREDENTIALS
    }

    public enum DatabaseSource {
        CLOUD, LOCAL, NONE
    }

    public record AuthResult(boolean success, UserAccount user, ErrorType errorType, DatabaseSource used) {
        public static AuthResult ok(UserAccount u, DatabaseSource used) { return new AuthResult(true, u, ErrorType.NONE, used); }
        public static AuthResult invalid(DatabaseSource used) { return new AuthResult(false, null, ErrorType.INVALID_CREDENTIALS, used); }
        public static AuthResult error(ErrorType t) { return new AuthResult(false, null, t, DatabaseSource.NONE); }
    }

    @FunctionalInterface
    public interface AuthProgressListener {
        /**
         * type: "update" -> arg = String message
         *       "before" -> arg = DatabaseSource that will be attempted
         *       "after" -> arg = AttemptResult
         */
        void onProgress(String type, Object arg);
    }

    private final UserAccountDao dao = new UserAccountDao();

    // Compatibilidad: authenticate sin listener
    public AuthResult authenticate(String identifier, String rawPassword) {
        return authenticate(identifier, rawPassword, null);
    }

    // Autenticación principal con listener
    public AuthResult authenticate(String identifier, String rawPassword, AuthProgressListener listener) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(rawPassword);

        DatabaseConnectionFactory.Mode initialMode = DatabaseConnectionFactory.getMode();

        try {
            if (initialMode == DatabaseConnectionFactory.Mode.CLOUD) {
                // --- Intento Cloud primero (pero solo si hay internet) ---
                if (!hasInternetFast()) {
                    if (listener != null) listener.onProgress("update", "No internet detected for cloud. Skipping cloud and trying local...");
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, false, ErrorType.CLOUD_NO_INTERNET));
                    // Directamente intentar local
                    if (listener != null) listener.onProgress("before", DatabaseSource.LOCAL);
                    AuthResult local = attemptLocal(identifier, rawPassword, listener);
                    if (local != null) return local;
                    return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
                } else {
                    if (listener != null) listener.onProgress("before", DatabaseSource.CLOUD);
                    AuthResult cloud = attemptCloud(identifier, rawPassword, listener);
                    if (cloud != null) return cloud; // puede ser ok o invalid
                    // si cloud devolvió null (problema de conexión), intentar local
                    if (listener != null) listener.onProgress("before", DatabaseSource.LOCAL);
                    AuthResult local = attemptLocal(identifier, rawPassword, listener);
                    if (local != null) return local;
                    return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
                }
            } else {
                // --- Intento Local primero ---
                if (listener != null) listener.onProgress("before", DatabaseSource.LOCAL);
                AuthResult local = attemptLocal(identifier, rawPassword, listener);
                if (local != null) return local;

                // Local falló; si hay internet, intentar cloud (pero avisar si no hay internet)
                if (!hasInternetFast()) {
                    if (listener != null) listener.onProgress("update", "No internet detected for cloud. Cannot try cloud.");
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, false, ErrorType.CLOUD_NO_INTERNET));
                    return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
                } else {
                    if (listener != null) listener.onProgress("before", DatabaseSource.CLOUD);
                    AuthResult cloud = attemptCloud(identifier, rawPassword, listener);
                    if (cloud != null) return cloud;
                    return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
                }
            }
        } catch (Exception e) {
            return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
        }
    }

    /* ---------- intentos concretos (devuelven AuthResult si se pudo consultar, o null si hubo un fallo de conexión) ---------- */

    private AuthResult attemptCloud(String identifier, String rawPassword, AuthProgressListener listener) {
        // Nota: en esta versión, authenticate ya aseguró que hay internet antes de llamar attemptCloud.
        DatabaseConnectionFactory.Mode previous = DatabaseConnectionFactory.getMode();
        try {
            DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
            try (Connection con = DatabaseConnectionFactory.getConnection()) {
                Optional<UserAccount> user = dao.findByIdentifierAndPassword(con, identifier, rawPassword);
                if (user.isPresent()) {
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, true, ErrorType.NONE));
                    return AuthResult.ok(user.get(), DatabaseSource.CLOUD);
                } else {
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, false, ErrorType.INVALID_CREDENTIALS));
                    return AuthResult.invalid(DatabaseSource.CLOUD);
                }
            }
        } catch (SQLException ex) {
            if (listener != null) listener.onProgress("update", "Cloud DB connection failed: " + ex.getMessage());
            if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, false, ErrorType.CLOUD_UNAVAILABLE));
            return null;
        } finally {
            DatabaseConnectionFactory.setMode(previous);
        }
    }

    private AuthResult attemptLocal(String identifier, String rawPassword, AuthProgressListener listener) {
        DatabaseConnectionFactory.Mode previous = DatabaseConnectionFactory.getMode();
        try {
            DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);
            try (Connection con = DatabaseConnectionFactory.getConnection()) {
                Optional<UserAccount> user = dao.findByIdentifierAndPassword(con, identifier, rawPassword);
                if (user.isPresent()) {
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.LOCAL, true, ErrorType.NONE));
                    return AuthResult.ok(user.get(), DatabaseSource.LOCAL);
                } else {
                    if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.LOCAL, false, ErrorType.INVALID_CREDENTIALS));
                    return AuthResult.invalid(DatabaseSource.LOCAL);
                }
            }
        } catch (SQLException ex) {
            if (listener != null) listener.onProgress("update", "Local DB connection failed: " + ex.getMessage());
            if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.LOCAL, false, ErrorType.LOCAL_UNAVAILABLE));
            return null;
        } finally {
            DatabaseConnectionFactory.setMode(previous);
        }
    }

    // Ping rápido para saber si hay internet
    public boolean hasInternetFast() {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("8.8.8.8", 53), 900); // 900ms timeout
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // POJO para enviar el resultado de cada intento al listener
    public static final class AttemptResult {
        public final DatabaseSource source;
        public final boolean success;
        public final ErrorType errorType;

        public AttemptResult(DatabaseSource source, boolean success, ErrorType errorType) {
            this.source = source;
            this.success = success;
            this.errorType = errorType;
        }
    }
}