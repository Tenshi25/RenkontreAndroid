package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;
import master.ccm.renkontreandroid.services.GpsService;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap maGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                },
                123);

        Intent i = new Intent(getApplicationContext(), GpsService.class);

        SupportMapFragment monSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map_activity_map);
        monSupportMapFragment.getMapAsync(this);

        startService(i);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.maGoogleMap = googleMap;
        // this.maGoogleMap.setMinZoomPreference(12);

        GeoLocationPosition userPosition = CurrentUser.getInstance().getGeoLocationPosition();

        if(userPosition != null && userPosition.getLatitude() != 0.0 && userPosition.getLatitude() != 0.0){
            LatLng userGeoPosition= new LatLng(userPosition.getLatitude(), userPosition.getLongitude());
            this.maGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(userGeoPosition));
            googleMap.addMarker(new MarkerOptions().position(userGeoPosition).title("Moi"));
        }

        this.maGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        this.maGoogleMap.getUiSettings().setCompassEnabled(true);

        this.maGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float valeurZoom = maGoogleMap.getCameraPosition().zoom;
                Toast.makeText(MapActivity.this, "zomm = "+valeurZoom, Toast.LENGTH_SHORT).show();
            }
        });

        this.maGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapActivity.this, "marker = "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        this.maGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapActivity.this, "Arrete de maltraiter le markeur", Toast.LENGTH_SHORT).show(); }
        });
    }
}
