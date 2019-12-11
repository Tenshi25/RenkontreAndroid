package master.ccm.renkontreandroid.utils;

public class GpsUtils {

    private static final int EARTH_RADIUS_IN_KM = 6371;

    public GpsUtils() {
    }

    public static double distanceInKmBetweenTwoMarker(double latitude1, double longitude1, double latitude2, double longitude2) {
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        double sineDistance = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngleBetweenTwoPoints = 2 * Math.atan2(Math.sqrt(sineDistance), Math.sqrt(1 - sineDistance));

        return EARTH_RADIUS_IN_KM * centralAngleBetweenTwoPoints;
    }

}
