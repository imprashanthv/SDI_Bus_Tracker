package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN=0;
    Button signOutbtn;
    Button driver;
    Button user;
    TextView halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Log.d("Auth",auth.getCurrentUser().getEmail());
            String name= auth.getCurrentUser().getDisplayName();
            halo=findViewById(R.id.haloId);
            halo.setText("Halo "+name+"!");
        }
        else {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setLogo(R.drawable.logo)
                            .setAvailableProviders(Arrays.asList
                                    (new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                    )
                            ).build(), RC_SIGN_IN);
        }



        signOutbtn=(Button) findViewById(R.id.signOutBtnId);
        driver =(Button)findViewById(R.id.driverBtnId);
        user = (Button)findViewById(R.id.userBtnId);

        signOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                finish();
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverActInt = new Intent(getApplicationContext(),MasterPassword.class);
                startActivity(driverActInt);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userActInt = new Intent(getApplicationContext(),layout_user.class);
                startActivity(userActInt);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}