package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Manager.UserDBManager;
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
                // Toast.makeText(MapActivity.this, "zoom = "+valeurZoom, Toast.LENGTH_SHORT).show();
            }
        });

        this.maGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Toast.makeText(MapActivity.this, "marker = "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        this.maGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapActivity.this, "Arrete de maltraiter le markeur", Toast.LENGTH_SHORT).show(); }
        });

        setFriendsAndEnemiesInMap();
    }

    private void setFriendsAndEnemiesInMap() {
        Log.i("SizeFriendList","size :" + CurrentUser.getInstance().getFriendslist().size());
        Log.i("SizeEnemyList","size :"+ CurrentUser.getInstance().getEnemylist().size());


        CurrentUser.getInstance().getFriendslist().forEach(user -> addFriendToMap(user));

        CurrentUser.getInstance().getEnemylist().forEach(user -> addEnemyToMap(user));
    }

    private void addFriendToMap(User friend) {
        String titleMessage = "";

        if (friend.getName() != null) {
            titleMessage = friend.getName();
        }

        if (friend.getLastName() != null) {
            titleMessage = titleMessage + " " + friend.getLastName();
        }

        titleMessage = titleMessage + " (" + friend.getMail() + ")";

        Log.i("FriendAddInMap","ami ajouté :"+ friend.getMail());

        GeoLocationPosition position = friend.getGeoLocationPosition();

        Log.i("FriendAddInMap","initialized position :"+ (position!=null));

        if (position != null) {
            LatLng userGeoPosition= new LatLng(position.getLatitude(), position.getLongitude());
            this.maGoogleMap.addMarker(new MarkerOptions().position(userGeoPosition)
                    .title(titleMessage)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    private void addEnemyToMap(User enemy) {
        String titleMessage = "";

        if (enemy.getName() != null) {
            titleMessage = enemy.getName();
        }

        if (enemy.getLastName() != null) {
            titleMessage = titleMessage + " " + enemy.getLastName();
        }

        titleMessage = titleMessage + " (" + enemy.getMail() + ")";
        Log.i("EnemyAddInMap","ennemi ajouté :"+ enemy.getMail());

        GeoLocationPosition position = enemy.getGeoLocationPosition();

        Log.i("EnemyAddInMap","initialized position :"+ (position!=null));

        if (position != null) {
            LatLng userGeoPosition= new LatLng(position.getLatitude(), position.getLongitude());
            this.maGoogleMap.addMarker(new MarkerOptions().position(userGeoPosition)
                    .title(titleMessage)
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }
    }
}
