package mx.edu.utez.warehousemanagerfx.models;

public class Administrator extends UserAccount {
    private Integer idBranch;
    private boolean isDeleted;

    public Administrator() {}

    public Administrator(int idUser, String fullName, String email, String phone, String username, String passwordKey, String roleType,
                         Integer idBranch, boolean isDeleted) {
        super(idUser, fullName, email, phone, username, passwordKey, roleType);
        this.idBranch = idBranch;
        this.isDeleted = isDeleted;
    }

    public Integer getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(Integer idBranch) {
        this.idBranch = idBranch;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "idBranch=" + idBranch +
                ", isDeleted=" + isDeleted +
                '}';
    }
}