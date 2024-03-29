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

/**
 * Classe de l'activité qui permet de s'inscrire
 */
public class Inscription_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText champEmail;
    private EditText champMDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inscription);
        mAuth = FirebaseAuth.getInstance();

        champEmail=findViewById(R.id.Et_mail_inscription);
        champMDP=findViewById(R.id.Et_password_inscription);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    /**
     * Mettre à jour l'UI de l'activité
     */
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            User user = new User();

            user.setLastName(currentUser.getDisplayName()) ;
            user.setMail(currentUser.getEmail());
            user.setPhone(currentUser.getPhoneNumber());
            UserDBManager userBDManager = new UserDBManager();
            userBDManager.VerifUserExistBeforeInsert(user,this);
        }

    }

    /**
     * Réaliser l'enregistrement de l'utilisateur sur Firebase.
     */
    private void inscriptionFirebase(String email,String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Inscription", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Inscription", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Inscription_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /**
     * Retourner à l'activité précédente
     */
    public void onClickRetour(View view) {
        Intent intent = new Intent(this, Connexion_activity.class);
        startActivity(intent);
        finish();
    }

    /**
     * S'enregistrer sur l'application en tant qu'utilisateur avec mail et mot de passe
     */
    public void onClickSignUp(View view) {
        String email = champEmail.getText().toString();
        String password = champMDP.getText().toString();
        inscriptionFirebase(email,password);
    }

    /**
     * En cas de succès d'enregistrement
     */
    public void RegisterSucess(String id, String name) {
        Intent intent = new Intent(this, Accueil_activity.class);
        startActivity(intent);
        finish();
    }

    /**
     * En cas d'échec d'enregistrement
     */
    public void RegistertFail() {
    }
}
