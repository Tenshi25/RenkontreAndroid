package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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
import master.ccm.renkontreandroid.services.NotificationPhoneService;
import master.ccm.renkontreandroid.services.RefreshMapUiService;
import master.ccm.renkontreandroid.utils.GpsUtils;
import master.ccm.renkontreandroid.utils.PermissionUtils;

/**
 * Classe de notre activité affichant la carte
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap maGoogleMap;
    private ArrayList<String> contactPhoneNumbers;
    private Map<String, String> userNumberMap;
    private Double maxDistanceInKm;
    private EditText editTextMaxDistanceInKm;
    private MarkerOptions myMarker;


    /**
     * Instancie à la création de la map les variables associés et lance le service de GPS utilisateur
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        PermissionUtils.askAllPermission(this);
        this.userNumberMap = new HashMap<>();
        this.maxDistanceInKm = 9999999.99;
        this.editTextMaxDistanceInKm = findViewById(R.id.id_map_value_max_distance);
        this.editTextMaxDistanceInKm.setText(this.maxDistanceInKm.toString());
        this.contactPhoneNumbers = getAllContactNumbers();
        this.contactPhoneNumbers.forEach(number -> Log.i("AllContacts","number :" + number));

        this.editTextMaxDistanceInKm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    MapActivity.this.maxDistanceInKm = Double.valueOf(String.valueOf(s));
                    changeVisibilityMarkerInUnacceptedDistance();
                }
            }
        });

        Intent i = new Intent(getApplicationContext(), GpsService.class);

        SupportMapFragment monSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map_activity_map);
        monSupportMapFragment.getMapAsync(this);

        startService(i);
    }

    /**
     * Lance les services de notifications et rafraichissement juste après création de l'activité
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        RefreshMapUiService.setMapActivity(this);
        Intent refreshMapIntent = new Intent(getApplicationContext(), RefreshMapUiService.class);
        startService(refreshMapIntent);

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationPhoneService.class);
        startService(notificationIntent);
    }

    /**
     * Code exécuté à la destruction de l'activité
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * Instancie la map avec nos settings et marqueurs si il y a besoin
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.maGoogleMap = googleMap;
        this.maGoogleMap.setMinZoomPreference(5);

        GeoLocationPosition userPosition = CurrentUser.getInstance().getGeoLocationPosition();

        if(userPosition != null && userPosition.getLatitude() != 0.0 && userPosition.getLatitude() != 0.0){
            LatLng userGeoPosition= new LatLng(userPosition.getLatitude(), userPosition.getLongitude());
            this.maGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(userGeoPosition));
            MarkerOptions myMarkerOptions = new MarkerOptions().position(userGeoPosition).title("Moi");
            googleMap.addMarker(myMarkerOptions);
            this.myMarker = myMarkerOptions;
        }

        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
        this.maGoogleMap.animateCamera(zoom);

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

    /**
     * Ajoute amis et ennemies sur la map si il y'a
     */
    private void setFriendsAndEnemiesInMap() {
        Log.i("SizeFriendList","size :" + CurrentUser.getInstance().getFriendslist().size());
        Log.i("SizeEnemyList","size :"+ CurrentUser.getInstance().getEnemylist().size());


        CurrentUser.getInstance().getFriendslist().forEach(user -> addFriendToMap(user));

        CurrentUser.getInstance().getEnemylist().forEach(user -> addEnemyToMap(user));
    }

    /**
     * Ajoute marqueur d'ami sur la map, si ce dernier à des données de GPS (latitude, longitude)
     */
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
            MarkerOptions friendMarker = new MarkerOptions().position(userGeoPosition)
                    .title(titleMessage)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            if (GpsUtils.distanceInKmBetweenTwoMarker(myMarker.getPosition().latitude, myMarker.getPosition().longitude,
                    friendMarker.getPosition().latitude, friendMarker.getPosition().longitude) <= this.maxDistanceInKm) {
                this.maGoogleMap.addMarker(friendMarker);
            }
        }
    }

    /**
     * Ajoute marqueur d'ennemi sur la map, si ce dernier à des données de GPS (latitude, longitude)
     */
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
            LatLng userGeoPosition = new LatLng(position.getLatitude(), position.getLongitude());
            MarkerOptions enemyMarker = new MarkerOptions().position(userGeoPosition)
                    .title(titleMessage)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            if (GpsUtils.distanceInKmBetweenTwoMarker(myMarker.getPosition().latitude, myMarker.getPosition().longitude,
                    enemyMarker.getPosition().latitude, enemyMarker.getPosition().longitude) <= this.maxDistanceInKm) {
                this.maGoogleMap.addMarker(enemyMarker);
            }
        }
    }

    /**
     * Récupére la liste des numéros de téléphone enregistrés dans le répertoire du téléphone
     * @return ArrayList qui est la liste des numéros récupérés
     */
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

    /**
     * Vérifie qu'un numéro de téléphone fait parti du répertoire téléphonique
     * @param number
     * @return boolean qui est le résultat
     */
    private boolean isANumberOfOwnContacts(String number) {
        if (number.contains("+")){
            return contactPhoneNumbers.contains(number);
        } else {
            return contactPhoneNumbers.contains(number) || contactPhoneNumbers.contains("+33"+number.substring(1));
        }
    }

    /**
     * Réinitialise les marqueurs de la carte et relance l'ajout des marqueurs
     */
    public void changeVisibilityMarkerInUnacceptedDistance() {
        if (this.maGoogleMap != null) {
            this.maGoogleMap.clear();

            GeoLocationPosition userPosition = CurrentUser.getInstance().getGeoLocationPosition();

            if(userPosition != null && userPosition.getLatitude() != 0.0 && userPosition.getLatitude() != 0.0){
                LatLng userGeoPosition= new LatLng(userPosition.getLatitude(), userPosition.getLongitude());
                MarkerOptions myMarkerOptions = new MarkerOptions().position(userGeoPosition).title("Moi");
                this.maGoogleMap.addMarker(myMarkerOptions);
                this.myMarker = myMarkerOptions;
            }

            this.setFriendsAndEnemiesInMap();
        }
    }
}
