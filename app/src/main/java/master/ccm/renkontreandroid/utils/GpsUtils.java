package master.ccm.renkontreandroid.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.startActivity;

/**
 * Classe de notre utilitaire pour le GPS
 */
public class GpsUtils {

    private static final int EARTH_RADIUS_IN_KM = 6371;

    /**
     * Constructeur par défaut
     */
    public GpsUtils() {
    }

    /**
     * Retourne la distance entre deux points de la Terre
     * @param latitude1 latitude de l'utilisateur courant
     * @param longitude1 longitude de l'utilisateur courant
     * @param latitude2 latitude de l'utilisateur ami/ennemi
     * @param longitude2 longitude de l'utilisateur ami/ennemi
     * @return double pour la distance obtenu
     */
    public static double distanceInKmBetweenTwoMarker(double latitude1, double longitude1, double latitude2, double longitude2) {
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        double sineDistance = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double centralAngleBetweenTwoPoints = 2 * Math.atan2(Math.sqrt(sineDistance), Math.sqrt(1 - sineDistance));

        return EARTH_RADIUS_IN_KM * centralAngleBetweenTwoPoints;
    }

    /**
     * Affecte au user (ses données de géolocalisation) la dernière position GPS connu du téléphone
     */
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

    /**
     * Vérifie si le GPS est activé
     */
    public static void needActiveLocalization(AppCompatActivity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(activity);

        }
    }

    /**
     * Fabriquer un message alertant que le GPS n'est pas activé et proposer son activation
     */
    private static void buildAlertMessageNoGps(AppCompatActivity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it (it will be necessary to share your position) ? If you refuse your localization can be incorrect in the map or only your friends and ennemies can appear on the map but not you")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(activity, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
