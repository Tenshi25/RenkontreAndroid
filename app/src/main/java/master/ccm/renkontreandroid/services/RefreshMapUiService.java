package master.ccm.renkontreandroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import master.ccm.renkontreandroid.Manager.UserDBManager;
import master.ccm.renkontreandroid.MapActivity;
import master.ccm.renkontreandroid.services.interfaces.RefreshMapService;

public class RefreshMapUiService extends Service implements RefreshMapService {

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
        userDBManager = new UserDBManager();
        userDBManager.addAllListener();
    }

    public static void refreshMap() {
        if (mapActivity != null){
            mapActivity.changeVisibilityMarkerInUnacceptedDistance();
        }
    }

    public static void setMapActivity(MapActivity mapActivity) {
        RefreshMapUiService.mapActivity = mapActivity;
    }
}
