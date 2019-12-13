package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.utils.GpsUtils;

public class Accueil_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView tv_mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_accueil);

        mAuth = FirebaseAuth.getInstance();
        tv_mail=findViewById(R.id.id_mail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // On test si l'utilisateur est connécté et son nom et son name
            String name = user.getDisplayName();
            String email = user.getEmail();
            tv_mail.setText(email);

            GpsUtils.getLastGpsKnownData(this);
        }
    }
    //vers la gestion du profile
    public void onClickProfile(View view){
        Intent intent = new Intent(this, Profile_activity.class);
        startActivity(intent);
        finish();

    }

    //vers la gestion des amis et ennemis
    public void onClickFriendsEnemyList(View view){
        Intent intent = new Intent(this, ListFriendsEnemy_activity.class);
        startActivity(intent);
        finish();

    }


    public void onClickMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }


}
