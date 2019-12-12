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

public class NotificationPhoneService extends Service {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static NotificationPhoneService staticContext;
    private static Map<String, String> mapNotificationId;

    public NotificationPhoneService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        staticContext = this;
        mapNotificationId = new HashMap<>();
    }


    public static void notifyUserInProximity(User user, boolean isAFriend) {
        int notificationId = getRandomNumberInRange(1, 999999999);
        if (staticContext != null) {
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

            createNotificationToShow(user, isAFriend, notificationId, distance);
        }
    }


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

    private static String createNotificationTitle(boolean isAFriend) {
        if (isAFriend){
            return "Rencontrez votre ami";
        }

        return "Fuyez votre ennemi";
    }

    private static Bitmap createNotificationLargeIcon(boolean isAFriend) {
        if (isAFriend){
            return BitmapFactory.decodeResource(staticContext.getResources(), R.drawable.amis);
        }

        return BitmapFactory.decodeResource(staticContext.getResources(), R.drawable.ennemi);
    }

    private static int createNotificationSmallIcon() {
            return R.drawable.ic_notification_icon;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
