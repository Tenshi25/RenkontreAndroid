package master.ccm.renkontreandroid.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import master.ccm.renkontreandroid.services.interfaces.PositionTrackingService;

/**
 * Classe du service de GPS qui trace la localisation de l'utilisateur
 */
public class GpsService extends Service implements PositionTrackingService {

    private LocationManager locationManager;

    /**
     * Constructeur par défaut
     */
    public GpsService() {
    }

    /**
     * Implemente la méthode onBind pour un service non lié
     * @param intent
     * @return Ibinder null car service non lié
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Instancie des variable à la création du service et créer un broadcast receiver pour le GPS
     */
    @Override
    public void onCreate() {
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        this.trackingLocationAsync();
    }

    /**
     * Ecoute la localisation du téléphone quand elle change (au bout de 3 secondes)
     * A chaque changement, notifie le broadcast receiver associé
     */
    @Override
    public void trackingLocationAsync() {
        Intent intent = new Intent(this, GpsUpdateReceiver.class);

        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.d("PERMISSION", "Aucun droit");

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, pending);
    }

    /**
     * Code éxécuté à la destruction du service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
