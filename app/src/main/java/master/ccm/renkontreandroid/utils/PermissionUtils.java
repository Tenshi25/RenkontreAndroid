package master.ccm.renkontreandroid.utils;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Classe de notre utilitaire pour les permissions
 */
public class PermissionUtils {

    /**
     * Constructeur par défaut
     */
    public PermissionUtils() {
    }

    /**
     * Demander l'ensemble des permissions
     */
    public static void askAllPermission(AppCompatActivity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE

                },
                123);
    }
}
