package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Manager.UserDBManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Form_add_friends_enemy_activity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText et_friendEnemyMail;
    private String friendOrEnemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_form_add_friends_enemy);

        et_friendEnemyMail = findViewById(R.id.et_userMail_form_add_friends_enemy);


        Spinner spinner = (Spinner) findViewById(R.id.id_spinner_form_add_friend_enemy);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.FriendEnemy_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }
    public void onClickAdd(View view) {
        UserDBManager userDBManager = new UserDBManager();
        /*User newFriendEnemy = new User();
        newFriendEnemy.setMail(et_friendEnemyMail.getText().toString());
        */
        //userDBManager.selectBeforeUpdateUserFriendsEnemy(newFriendEnemy,friendOrEnemy,this);
        if(et_friendEnemyMail.getText().toString().equals(CurrentUser.getInstance().getMail()))
        {
            Toast.makeText(this,"Petit(e) malin(e) ! On ne peut pas s'ajouter soi-même",Toast.LENGTH_SHORT).show();
        }else {
            userDBManager.BeforeAddUserLink(et_friendEnemyMail.getText().toString(), friendOrEnemy, this);
        }
    }

    public void onClickReturn(View view) {
        Intent intent = new Intent(this, ListFriendsEnemy_activity.class);
        startActivity(intent);
        finish();
    }


    public void updateUserFriendsEnemySuccess() {
        Toast.makeText(this,"votre liste à été mise à jour",Toast.LENGTH_LONG).show();
    }

    public void updateUserFriendsEnemyFailed() {
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        friendOrEnemy = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void userNotExist() {
        Toast.makeText(this,"L'utilisateur n'est pas inscrit",Toast.LENGTH_SHORT).show();
    }

    public void userExist(String id) {

        Toast.makeText(this,"L'utilisateur est inscrit",Toast.LENGTH_SHORT).show();
    }

    public void AddLinkSucess() {
        Toast.makeText(this,"votre liste à été mise à jour",Toast.LENGTH_LONG).show();
    }
}
