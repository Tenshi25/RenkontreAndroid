package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Classe de l'activité qui permet d'appeler l'activité de SMS ou d'appel via un numéro de téléphone
 */
public class ContactPhoneActivity extends AppCompatActivity {

    private String phoneNumber;
    private String identity;

    /**
     * Instancie les variables à la création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_phone);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundleContact");

        this.identity = bundle.getString("identity");
        this.phoneNumber = bundle.getString("phone");

        TextView textViewIdentity = findViewById(R.id.id_contact_identity);
        textViewIdentity.setText(identity);
    }

    /**
     * Via un numéro de téléphone lance l'activité de SMS du téléphone pour ce contact sur le clic du bouton SMS
     */
    public void onClickAskSmsActivity(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", this.phoneNumber, null)));
    }

    /**
     * Lance l'appel vers ce numéro en cliquant sur le bouton Call
     */
    public void onClickAskCallActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.phoneNumber));
        startActivity(intent);
    }

    /**
     * Ferme l'activité sur le bouton Retour
     */
    public void onClickReturnBefore(View view) {
        finish();
    }
}
