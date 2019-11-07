package master.ccm.renkontreandroid.Entity;

import java.util.Date;

public class GeoLocationPosition {

    private double longitude;

    private double latitude;

    public Date dateRegistration;

    public GeoLocationPosition() {

    }

    public GeoLocationPosition(double longitude, double latitude, Date dateRegistration) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateRegistration = dateRegistration;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDateRegistration() {
        return dateRegistration;
    }

    public void setDateRegistration(Date dateRegistration) {
        this.dateRegistration = dateRegistration;
    }
}