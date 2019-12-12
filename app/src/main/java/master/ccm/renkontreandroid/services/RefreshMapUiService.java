package master.ccm.renkontreandroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import master.ccm.renkontreandroid.Manager.UserDBManager;
import master.ccm.renkontreandroid.MapActivity;
import master.ccm.renkontreandroid.services.interfaces.RefreshMapService;

public class RefreshMapUiService extends Service implements RefreshMapService {

    private static Handler handler;
    private static Runnable timedTask;
    private static UserDBManager userDBManager;
    private static MapActivity mapActivity;

    public RefreshMapUiService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();

        userDBManager = new UserDBManager();

        timedTask = new Runnable(){

            @Override
            public void run() {
                runRepetitiveTask();
                handler.postDelayed(timedTask, 10000);
            }};

        handler.post(timedTask);
    }

    public static void refreshMap() {
        mapActivity.changeVisibilityMarkerInUnacceptedDistance();

    }

    @Override
    public void runRepetitiveTask() {
        userDBManager.selectAllFriendsEnemyRefresh();
    }

    public static void setMapActivity(MapActivity mapActivity) {
        RefreshMapUiService.mapActivity = mapActivity;
    }
}
