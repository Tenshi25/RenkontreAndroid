package master.ccm.renkontreandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactPhoneActivity extends AppCompatActivity {

    private String phoneNumber;
    private String identity;

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

    public void onClickAskSmsActivity(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", this.phoneNumber, null)));
    }

    public void onClickAskCallActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.phoneNumber));
        startActivity(intent);
    }

    public void onClickReturnBefore(View view) {
        finish();
    }
}
