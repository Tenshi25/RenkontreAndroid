package master.ccm.renkontreandroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import master.ccm.renkontreandroid.Manager.UserDBManager;
import master.ccm.renkontreandroid.MapActivity;

/**
 * Classe du BroadcastReceiver du GPS qui trace la localisation de l'utilisateur
 */
public class GpsUpdateReceiver extends BroadcastReceiver {

    /**
     * A chaque changement de données GPS, modifie la données en base de données de l'utilisateur
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        if (location != null){
            Log.i("GPS", "Latitude"+location.getLatitude());
            Log.i("GPS", "Longitude"+location.getLongitude());

            UserDBManager userDBManager = new UserDBManager();
            userDBManager.updateUserToAddPositionService(location);
        }
    }
}
