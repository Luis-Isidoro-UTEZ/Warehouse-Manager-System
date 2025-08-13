package mx.edu.utez.warehousemanagerfx.models;

public class UserAccount {
    private int idUser;
    private String fullName;
    private String email;
    private String phone;
    private String username;
    private String passwordKey; // Hashed password
    private String roleType; // "SUPERADMINISTRATOR" o "ADMINISTRATOR"

    public UserAccount() {
    }

    public UserAccount(int idUser, String fullName, String email, String phone,
                       String username, String passwordKey, String roleType) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.passwordKey = passwordKey;
        this.roleType = roleType;
    }

    // Getters y setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordKey() { return passwordKey; }
    public void setPasswordKey(String passwordKey) { this.passwordKey = passwordKey; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    @Override
    public String toString() {
        return "UserAccount{" +
                "idUser=" + idUser +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", roleType='" + roleType + '\'' +
                '}';
    }
}
