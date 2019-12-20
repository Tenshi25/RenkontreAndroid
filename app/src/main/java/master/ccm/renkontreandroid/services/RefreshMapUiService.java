package master.ccm.renkontreandroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import master.ccm.renkontreandroid.Manager.UserDBManager;
import master.ccm.renkontreandroid.MapActivity;

/**
 * Classe du service de la map qui actualise la map à chaque changement de données utilisateur courant, amis et ennemis associés
 */
public class RefreshMapUiService extends Service {

    private static UserDBManager userDBManager;
    private static MapActivity mapActivity;

    /**
     * Constructeur par défaut
     */
    public RefreshMapUiService() {
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
     * Instancie la variable et ajoute tous les listeners, écoutes des données en base nécessaire
     */
    @Override
    public void onCreate() {
        userDBManager = new UserDBManager();
        userDBManager.addAllListener();
    }

    /**
     * Rafraichit la map via méthode statique de l'objet MapActivity
     */
    public static void refreshMap() {
        if (mapActivity != null){
            mapActivity.changeVisibilityMarkerInUnacceptedDistance();
        }
    }

    /**
     * affecte la valeur mapActivity dans la variable mapActivity
     */
    public static void setMapActivity(MapActivity mapActivity) {
        RefreshMapUiService.mapActivity = mapActivity;
    }
}
