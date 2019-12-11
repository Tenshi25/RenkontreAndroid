package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.GeoLocationPosition;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.services.GpsService;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap maGoogleMap;
    private ArrayList<String> contactPhoneNumbers;
    private Map<String, String> userNumberMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE

                },
                123);

        this.userNumberMap = new HashMap<>();
        this.contactPhoneNumbers = getAllContactNumbers();
        this.contactPhoneNumbers.forEach(number -> Log.i("AllContacts","number :" + number));

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
                if (marker.getTitle().equals("Moi")) {
                    // do nothing
                } else {
                    String email = marker.getTitle().substring(marker.getTitle().indexOf("(") + 1, marker.getTitle().indexOf(")"));
                    String phoneNumber = userNumberMap.get(email);
                    if (phoneNumber != null) {
                        phoneNumber = phoneNumber.replace(" ", "");
                        Boolean isRegisteredContact = isANumberOfOwnContacts(phoneNumber);

                        if (isRegisteredContact){
                            Intent contactIntent = new Intent(MapActivity.this, ContactPhoneActivity.class);
                            Bundle contactContent = new Bundle();
                            contactContent.putString("phone", phoneNumber);
                            contactContent.putString("identity", marker.getTitle());
                            contactIntent.putExtra("bundleContact", contactContent);
                            startActivity(contactIntent);
                        }

                    } else {
                        Toast.makeText(MapActivity.this, "Phone number not registered by user", Toast.LENGTH_SHORT).show();
                    }
                }
            }
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
        this.userNumberMap.put(friend.getMail(), friend.getPhone());

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
        this.userNumberMap.put(enemy.getMail(), enemy.getPhone());

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

    private ArrayList<String> getAllContactNumbers() {
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {
            ArrayList<String> allContacts = new ArrayList<>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        allContacts.add(contactNumber.replace(" ", ""));
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;

            return allContacts;
        }

        return new ArrayList<>();
    }

    private boolean isANumberOfOwnContacts(String number){
        if (number.contains("+")){
            return contactPhoneNumbers.contains(number);
        } else {
            return contactPhoneNumbers.contains(number) || contactPhoneNumbers.contains("+33"+number.substring(1));
        }
    }
}
