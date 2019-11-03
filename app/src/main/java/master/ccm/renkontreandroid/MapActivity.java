package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import master.ccm.renkontreandroid.services.GpsService;

public class MapActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toast.makeText(this, "mapmonde", Toast.LENGTH_SHORT).show();

        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                },
                123);

        Intent i = new Intent(getApplicationContext(), GpsService.class);
        startService(i);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    double longitude = intent.getDoubleExtra("longitude", 1);
                    double latitude = intent.getDoubleExtra("latitude", 1);

                    String lat = String.valueOf(latitude);
                    String lng = String.valueOf(longitude);

                    Toast.makeText(MapActivity.this, lat, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MapActivity.this, lng, Toast.LENGTH_SHORT).show();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
