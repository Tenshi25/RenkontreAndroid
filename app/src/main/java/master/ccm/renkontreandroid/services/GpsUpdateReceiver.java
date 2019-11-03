package master.ccm.renkontreandroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import master.ccm.renkontreandroid.MapActivity;

public class GpsUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        Log.i("GPS", "Latitude"+location.getLatitude());
        Log.i("GPS", "Longitude"+location.getLongitude());
    }
}
