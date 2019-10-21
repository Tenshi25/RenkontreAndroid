package master.ccm.renkontreandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(this, Accueil_activity.class);
        startActivity(intent);
        finish();
    }
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

    public void onClickRetour(View view) {
        Intent intent = new Intent(this, Connexion_activity.class);
        startActivity(intent);
        finish();
    }

    public void onClickSignUp(View view) {
        String email = champEmail.getText().toString();
        String password = champMDP.getText().toString();
        inscriptionFirebase(email,password);
    }
}
