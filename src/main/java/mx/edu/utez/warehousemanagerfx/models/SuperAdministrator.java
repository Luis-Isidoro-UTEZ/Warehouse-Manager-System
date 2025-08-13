package mx.edu.utez.warehousemanagerfx.models;

public class SuperAdministrator extends UserAccount {
    public SuperAdministrator() {}

    public SuperAdministrator(int idUser, String fullName, String email, String phone, String username, String passwordKey, String roleType) {
        super(idUser, fullName, email, phone, username, passwordKey, roleType);
    }

    @Override
    public String toString() {
        return "SuperAdministrator{" +
                "userData=" + super.toString() +
                '}';
    }
}