package mx.edu.utez.warehousemanagerfx.services;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.models.dao.UserAccountDao;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

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

    // Interfaz para informar progreso a quien llame (ej. el Controller).
    @FunctionalInterface
    public interface AuthProgressListener {
        /**
         * Recibe notificaciones de progreso. Se ejecuta en el mismo hilo donde authenticate() corre
         * (normalmente un worker thread), por eso el listener puede bloquear con Thread.sleep(...)
         * para forzar la pausa visual que quieras en la UI (la UI itself se actualiza via Platform.runLater).
         *
         * type: "update" -> texto libre para mostrar,
         * "before" -> se intentará la fuente indicada (arg = DatabaseSource),
         * "after"  -> resultado del intento (arg boolean success),
         *
         * Puedes usar null como listener si no quieres notificaciones.
         */
        void onProgress(String type, Object arg);
    }

    private final UserAccountDao dao = new UserAccountDao();

    // Versión compat: mantiene comportamiento anterior (sin listener)
    public AuthResult authenticate(String identifier, String rawPassword) {
        return authenticate(identifier, rawPassword, null);
    }

    // Nueva versión: puede recibir listener para recibir eventos de intento/resultado
    public AuthResult authenticate(String identifier, String rawPassword, AuthProgressListener listener) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(rawPassword);

        DatabaseConnectionFactory.Mode initialMode = DatabaseConnectionFactory.getMode();

        try {
            if (initialMode == DatabaseConnectionFactory.Mode.CLOUD) {
                // intento nube primero
                if (listener != null) listener.onProgress("before", DatabaseSource.CLOUD);
                AuthResult cloud = attemptCloud(identifier, rawPassword, listener);
                if (cloud != null) return cloud; // success or invalid credentials

                // si llegamos aquí, nube falló -> intentar local
                if (listener != null) listener.onProgress("before", DatabaseSource.LOCAL);
                AuthResult local = attemptLocal(identifier, rawPassword, listener);
                if (local != null) return local;

                return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
            } else {
                // intento local primero
                if (listener != null) listener.onProgress("before", DatabaseSource.LOCAL);
                AuthResult local = attemptLocal(identifier, rawPassword, listener);
                if (local != null) return local;

                // si local falla, intentar nube solo si hay internet
                if (hasInternetFast()) {
                    if (listener != null) listener.onProgress("before", DatabaseSource.CLOUD);
                    AuthResult cloud = attemptCloud(identifier, rawPassword, listener);
                    if (cloud != null) return cloud;
                } else {
                    if (listener != null) listener.onProgress("update", "No internet to try Cloud.");
                }

                return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
            }
        } catch (Exception e) {
            return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
        }
    }

    /* ---------- Métodos auxiliares que realizan el intento real ---------- */

    // Devuelve AuthResult (ok or invalid) si la conexión y consulta se realizaron; devuelve null si hubo problema de conexión
    private AuthResult attemptCloud(String identifier, String rawPassword, AuthProgressListener listener) {
        if (!hasInternetFast()) {
            if (listener != null) listener.onProgress("update", "No internet available for Cloud.");
            // informar error de internet
            if (listener != null) listener.onProgress("after", new AttemptResult(DatabaseSource.CLOUD, false, ErrorType.CLOUD_NO_INTERNET));
            return null;
        }

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

    // Ping rápido para saber si hay internet (mantener la implementación que ya tenías)
    public boolean hasInternetFast() {
        // Reimplementa tu lógica original (o deja la que ya tenías).
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("8.8.8.8", 53), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // POJO para enviar un resultado de intento por el listener (no es parte de la API pública)
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
