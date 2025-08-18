package mx.edu.utez.warehousemanagerfx.models;

public class SuperAdministrator extends UserAccount {
    private int idSuperAdmin;

    public SuperAdministrator() {}

    public SuperAdministrator(int idUser, String firstName, String middleName, String lastName, String secondLastName,
                              String email, String phone, String username, String passwordKey, String roleType) {
        super(idUser, firstName, middleName, lastName, secondLastName, email, phone, username, passwordKey, roleType);
        this.idSuperAdmin = idUser;
    }

    public int getIdSuperAdmin() {
        return idSuperAdmin;
    }

    public void setIdSuperAdmin(int idSuperAdmin) {
        this.idSuperAdmin = idSuperAdmin;
    }

    @Override
    public String toString() {
        return "SuperAdministrator{" +
                "userData=" + super.toString() +
                "idSuperAdmin=" + idSuperAdmin +
                '}';
    }
}