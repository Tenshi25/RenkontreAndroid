package master.ccm.renkontreandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import master.ccm.renkontreandroid.Entity.User;
import master.ccm.renkontreandroid.Manager.UserDBManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Connexion_activity extends AppCompatActivity {

    // déclaration de l'instande FirebaseAuth
    private FirebaseAuth mAuth;

    private EditText champEmail;
    private EditText champMDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_connexion);

        // initialisation de la variable Auth
        mAuth = FirebaseAuth.getInstance();

        champEmail=findViewById(R.id.Et_mail_connexion);
        champMDP=findViewById(R.id.Et_password_connexion);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            User user = new User();

            user.setLastName(currentUser.getDisplayName()) ;
            user.setMail(currentUser.getEmail());
            user.setPhone(currentUser.getPhoneNumber());
            UserDBManager userBDManager = new UserDBManager();
            userBDManager.ConnectUser(user,this);
        }

    }
    private void connexionFirebase(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("connexion Firebase", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("connexion Firebase", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Connexion_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    public void onClickInscription(View view) {
        Intent intent = new Intent(this, Inscription_activity.class);
        startActivity(intent);
        finish();
    }

    public void onClickSignIn(View view) {
        String email = champEmail.getText().toString();
        String password = champMDP.getText().toString();
        connexionFirebase(email,password);
    }

    public void ConnectionFailed() {
    }

    public void ConnectSucess(String id, String username) {
        Intent intent = new Intent(this, Accueil_activity.class);
        Toast.makeText(Connexion_activity.this, "Bienvenue"+username,
                Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
