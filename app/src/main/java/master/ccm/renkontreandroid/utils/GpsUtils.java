package master.ccm.renkontreandroid.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.appcompat.app.AppCompatActivity;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;

import static androidx.core.content.ContextCompat.checkSelfPermission;

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

    public static void getLastGpsKnownData(AppCompatActivity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            GeoLocationPosition position = new GeoLocationPosition();
            position.setLongitude(location.getLongitude());
            position.setLatitude(location.getLatitude());

            CurrentUser.getInstance().setGeoLocationPosition(position);
        }
    }

}
