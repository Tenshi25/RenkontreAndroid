package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;
import master.ccm.renkontreandroid.Entity.CurrentUser;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Manager.UserDBManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Profile_activity extends AppCompatActivity {
    private EditText et_name;
    private EditText et_lastname;
    private EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        et_lastname = findViewById(R.id.tb_lastname_profile);
        et_name = findViewById(R.id.tb_name_profile);
        et_phone= findViewById(R.id.tb_phone_profile);

        if(CurrentUser.getInstance().getLastName()!=null){
            et_lastname.setText(CurrentUser.getInstance().getLastName());
        }
        if(CurrentUser.getInstance().getName()!=null){
            et_name.setText(CurrentUser.getInstance().getName());
        }
        if(CurrentUser.getInstance().getPhone()!=null){
            et_phone.setText(CurrentUser.getInstance().getPhone());
        }
    }
    public void onClickReturn(View view){
        Intent intent = new Intent(this, Accueil_activity.class);
        startActivity(intent);
        finish();

    }
    public void onClickUpdate(View view){
        User userUpdate = new User();

        //ajouter depuis les interfaces

        userUpdate.setLastName(et_lastname.getText().toString());
        userUpdate.setName(et_name.getText().toString());
        userUpdate.setPhone(et_phone.getText().toString());

        CurrentUser.getInstance().setLastName(et_lastname.getText().toString());
        CurrentUser.getInstance().setName(et_name.getText().toString());
        CurrentUser.getInstance().setPhone(et_phone.getText().toString());

        UserDBManager userDBManager = new UserDBManager();
        userDBManager.updateUser(userUpdate,this);

    }
    public void updateUserSuccess() {
        Toast.makeText(this, "Votre profil à été mis à jour",
                Toast.LENGTH_SHORT).show();


    }

    public void updateUserFailed() {
        Toast.makeText(this, "ERREUR ! Votre profil n'a pas été mis à jour",
                Toast.LENGTH_SHORT).show();
    }
}
