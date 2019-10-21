package master.ccm.renkontreandroid.Entity;

public class Location {

    private double longitude;

    private double latitude;

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

    private double deg2rad(double d){
        return d * (Math.PI/180);
        //Distance en m.

    }
    public double getDistanceWithOtherPosition(Location loc){
        int R = 6371;
        double dLat = deg2rad(loc.getLatitude()-this.getLatitude());
        double dLon = deg2rad(loc.getLongitude()-this.getLongitude());
        double result = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(deg2rad(this.getLatitude())) * Math.cos(deg2rad(loc.getLatitude())) * Math.sin(dLon/2) * Math.sin(dLon/2);
        result = 2 * Math.atan2(Math.sqrt(result), Math.sqrt(1-result));
        result = R * result;
        return result*1000;
    }

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Location() {
    }
}
