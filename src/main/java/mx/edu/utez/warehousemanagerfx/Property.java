package mx.edu.utez.warehousemanagerfx;

public class Property {
    // Attributes
    private int idProperty;
    private String propertyType;
    private String country;
    private String state;
    private String municipality;
    private String postalCode;
    private String neighborhood;
    private String addressDetail;

    // Constructor Methods
    public Property() {
    }

    public Property(int idProperty, String propertyType, String country, String state, String municipality, String postalCode, String neighborhood, String addressDetail) {
        this.idProperty = idProperty;
        this.propertyType = propertyType;
        this.country = country;
        this.state = state;
        this.municipality = municipality;
        this.postalCode = postalCode;
        this.neighborhood = neighborhood;
        this.addressDetail = addressDetail;
    }

    // Class Methods
    @Override
    public String toString() {
        return "Property{" +
                "idProperty=" + idProperty +
                ", propertyType='" + propertyType + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", municipality='" + municipality + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                '}';
    }

    // Getters & Setters
    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
