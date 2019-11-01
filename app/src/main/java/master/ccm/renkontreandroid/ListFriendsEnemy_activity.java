package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListFriendsEnemy_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_friends_enemy);
    }

    public void onClickAddFriendsEnemy(View view) {
        Intent intent = new Intent(this, Form_add_friends_enemy_activity.class);
        startActivity(intent);
        finish();
    }

    public void onClickReturn(View view) {
        Intent intent = new Intent(this, Accueil_activity.class);
        startActivity(intent);
        finish();
    }
}
