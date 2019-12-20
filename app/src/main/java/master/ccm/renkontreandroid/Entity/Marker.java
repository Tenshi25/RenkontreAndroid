package master.ccm.renkontreandroid.Entity;

public class Marker {

    private String idUser;
    private Double latitude;
    private Double longitude;
    private boolean isAnEnemy;
    private String username;
    private String phoneNumber;

    public Marker() {}

    public Marker(String idUser, Double longitude, Double latitude, boolean isAnEnemy, String username, String phoneNumber) {
        this.idUser = idUser;
        this.longitude = longitude;
        this.latitude = latitude;
        this.isAnEnemy = isAnEnemy;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public String getIdUser() {
        return idUser;
    }

    public boolean isAnEnemy() {return isAnEnemy;}

    public void setAnEnemy(boolean anEnemy) {this.isAnEnemy = anEnemy;}

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
