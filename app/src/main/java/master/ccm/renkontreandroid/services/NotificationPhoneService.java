package master.ccm.renkontreandroid.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.R;
import master.ccm.renkontreandroid.utils.GpsUtils;

/**
 * Classe du service de notification (non lié et d'arrière plan) qui créer et envoi les notifications amis/ennemis
 */
public class NotificationPhoneService extends Service {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static NotificationPhoneService staticContext;
    private static Map<String, String> mapNotificationId;
    private static final int MAX_DISTANCE_METERS_PROXIMITY = 1000;

    /**
     * Constructeur par défaut
     */
    public NotificationPhoneService() {
    }

    /**
     * Implemente la méthode onBind pour un service non lié en arrière plan
     * @param intent
     * @return Ibinder null car service non lié
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Instancie les variable du service à la création
     */
    @Override
    public void onCreate() {
        staticContext = this;
        mapNotificationId = new HashMap<>();
    }

    /**
     * Notifie si l'utilisateur ami/ennemi est proche de nous dans un rayon de 1000 mètres
     * @param user l'utilisateur concerné
     * @param isAFriend est une valeur booléenne qui dit si il s'agit d'un ami ou pas
     */
    public static void notifyUserInProximity(User user, boolean isAFriend) {
        int notificationId = getRandomNumberInRange(1, 999999999);
        if (staticContext != null && user.getGeoLocationPosition() != null && CurrentUser.getInstance().getGeoLocationPosition() != null) {
            String storedIndexNotification = mapNotificationId.get(user.getMail());
            if (storedIndexNotification != null) {
                notificationId = Integer.valueOf(storedIndexNotification);
            } else {
                mapNotificationId.put(user.getMail(), String.valueOf(notificationId));
            }

            CurrentUser currentUser = CurrentUser.getInstance();

            Double gpsDistanceInKm = GpsUtils.distanceInKmBetweenTwoMarker(
                    currentUser.getGeoLocationPosition().getLatitude(),
                    currentUser.getGeoLocationPosition().getLongitude(),
                    user.getGeoLocationPosition().getLatitude(),
                    user.getGeoLocationPosition().getLongitude());

            Double gpsDistanceInMeters = gpsDistanceInKm * 1000;
            int distance = gpsDistanceInMeters.intValue();

            if (distance <= MAX_DISTANCE_METERS_PROXIMITY) {
                createNotificationToShow(user, isAFriend, notificationId, distance);
            }
        }
    }


    /**
     * Créer la notification
     * @param user l'utilisateur concerné
     * @param isAFriend est une valeur booléenne qui dit si il s'agit d'un ami ou pas
     * @param notificationId est une valeur unique pour identifier la notification
     * @param distance est la distance entre l'utilisateur courant et l'utilisateur ciblé
     */
    private static void createNotificationToShow(User user, boolean isAFriend, int notificationId, int distance) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(staticContext, CHANNEL_ID)
                .setSmallIcon(createNotificationSmallIcon())
                .setLargeIcon(createNotificationLargeIcon(isAFriend))
                .setContentTitle(createNotificationTitle(isAFriend))
                .setContentText(createNotificationMessage(user, distance))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(createNotificationMessage(user, distance)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RenkontreAndroid";
            String description = "RenkontreAndroid";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = staticContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(staticContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * Créer le message la notification
     * @param user l'utilisateur concerné
     * @param distance est la distance entre l'utilisateur courant et l'utilisateur ciblé
     * @return String qui est le message
     */
    private static String createNotificationMessage(User user, int distance) {
        String notificationMessage = "";

        if (user.getName() != null) {
            notificationMessage = user.getName();
        }

        if (user.getLastName() != null) {
            notificationMessage = notificationMessage + " " + user.getLastName();
        }

        notificationMessage = notificationMessage + " (" + user.getMail() + ")";

        notificationMessage = notificationMessage + " est proche de vous  (environs "+ distance + " m)";

        return notificationMessage;
    }

    /**
     * Créer le titre de la notification
     * @param isAFriend est une valeur booléenne qui dit si il s'agit d'un ami ou pas
     * @return String qui est le titre
     */
    private static String createNotificationTitle(boolean isAFriend) {
        if (isAFriend){
            return "Rencontrez votre ami";
        }

        return "Fuyez votre ennemi";
    }

    /**
     * Créer l'icône large de la notification
     * @param isAFriend est une valeur booléenne qui dit si il s'agit d'un ami ou pas
     * @return Bitmap qui est le Bitmap associé à la large icône
     */
    private static Bitmap createNotificationLargeIcon(boolean isAFriend) {
        if (isAFriend){
            return BitmapFactory.decodeResource(staticContext.getResources(), R.drawable.amis);
        }

        return BitmapFactory.decodeResource(staticContext.getResources(), R.drawable.ennemi);
    }

    /**
     * Créer la petite icône de la notification
     * @return int qui est la valeur entière associé à la petite icône
     */
    private static int createNotificationSmallIcon() {
            return R.drawable.ic_notification_icon;
    }

    /**
     * Donne un numéro aléatoire qui va servir d'identifiant à la notifiaction
     * Valeur comprise entre min et max
     * @param min est une valeur minimale pour créer un identifiant de notification
     * @param max est une valeur maximale pour créer un identifiant de notification
     * @return int qui est la valeur entière généré
     */
    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
