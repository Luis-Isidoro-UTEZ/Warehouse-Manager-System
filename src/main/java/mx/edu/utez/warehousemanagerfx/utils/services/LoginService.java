package mx.edu.utez.warehousemanagerfx.utils.services;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.models.dao.UserAccountDao;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginService {

    public record AuthResult(boolean success, UserAccount user, ErrorType errorType) {
        public static AuthResult ok(UserAccount u) { return new AuthResult(true, u, ErrorType.NONE); }
        public static AuthResult invalid() { return new AuthResult(false, null, ErrorType.NONE); }
        public static AuthResult error(ErrorType t) { return new AuthResult(false, null, t); }
    }

    public enum ErrorType {
        NONE,
        CLOUD_NO_INTERNET,
        CLOUD_UNAVAILABLE,
        LOCAL_UNAVAILABLE,
        BOTH_UNAVAILABLE
    }

    private final UserAccountDao dao = new UserAccountDao();

    public AuthResult authenticate(String identifier, String rawPassword) {
        DatabaseConnectionFactory.Mode initialMode = DatabaseConnectionFactory.getMode();
        try {
            if (initialMode == DatabaseConnectionFactory.Mode.CLOUD) {
                AuthResult cloudResult = tryAuthCloud(identifier, rawPassword);
                if (cloudResult != null) return cloudResult;

                AuthResult localResult = tryAuthLocal(identifier, rawPassword);
                if (localResult != null) return localResult;

                return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
            } else {
                AuthResult localResult = tryAuthLocal(identifier, rawPassword);
                if (localResult != null) return localResult;

                if (hasInternetFast()) {
                    AuthResult cloudResult = tryAuthCloud(identifier, rawPassword);
                    if (cloudResult != null) return cloudResult;
                    return AuthResult.error(ErrorType.CLOUD_UNAVAILABLE);
                } else {
                    return AuthResult.error(ErrorType.LOCAL_UNAVAILABLE);
                }
            }
        } catch (Exception e) {
            return AuthResult.error(ErrorType.BOTH_UNAVAILABLE);
        }
    }

    private AuthResult tryAuthCloud(String identifier, String rawPassword) {
        if (!hasInternetFast()) {
            return AuthResult.error(ErrorType.CLOUD_NO_INTERNET);
        }

        DatabaseConnectionFactory.Mode previous = DatabaseConnectionFactory.getMode();
        try {
            DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
            try (Connection con = DatabaseConnectionFactory.getConnection()) {
                Optional<UserAccount> user = dao.findByIdentifierAndPassword(con, identifier, rawPassword);
                return user.map(AuthResult::ok).orElseGet(AuthResult::invalid);
            }
        } catch (SQLException ex) {
            return AuthResult.error(ErrorType.CLOUD_UNAVAILABLE);
        } finally {
            DatabaseConnectionFactory.setMode(previous);
        }
    }

    private AuthResult tryAuthLocal(String identifier, String rawPassword) {
        DatabaseConnectionFactory.Mode previous = DatabaseConnectionFactory.getMode();
        try {
            DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);
            try (Connection con = DatabaseConnectionFactory.getConnection()) {
                Optional<UserAccount> user = dao.findByIdentifierAndPassword(con, identifier, rawPassword);
                return user.map(AuthResult::ok).orElseGet(AuthResult::invalid);
            }
        } catch (SQLException ex) {
            return AuthResult.error(ErrorType.LOCAL_UNAVAILABLE);
        } finally {
            DatabaseConnectionFactory.setMode(previous);
        }
    }

    private boolean hasInternetFast() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1000); // 1s timeout
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
