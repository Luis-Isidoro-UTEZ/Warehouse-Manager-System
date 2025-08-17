package mx.edu.utez.warehousemanagerfx.utils.services;

import mx.edu.utez.warehousemanagerfx.models.UserAccount;

public class SessionManager { // Singleton
    private static UserAccount currentUser;

    private SessionManager() {
    }

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
