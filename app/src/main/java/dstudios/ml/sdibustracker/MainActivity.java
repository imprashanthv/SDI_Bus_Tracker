package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN=0;
    Button signOutbtn;
    Button driver;
    Button user;
    TextView halo;
    TextView weatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        weatherInfo = findViewById(R.id.weatherid);
        getWeather();

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
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,findViewById(R.id.driverBtnId),"driver");
                startActivity(driverActInt, compat.toBundle());
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userActInt = new Intent(getApplicationContext(),layout_user.class);
                //ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(findViewById(R.id.userBtnId),20,20,150,150);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,findViewById(R.id.weatherid),"user");
                startActivity(userActInt,compat.toBundle());
            }
        });
    }

    public void getWeather(){
        //Please do this - TODO request location permissions and show appropriate weather data
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=17.4833289&lon=78.5526347&appid=2387cfa1351c748c90cf99b6aff000de&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray weatherArray = response.getJSONArray("weather");
                    JSONObject weatherDescObject=  weatherArray.getJSONObject(0);
                    String desc = weatherDescObject.getString("description");
                    JSONObject tempObject = response.getJSONObject("main");
                    String temp = String.valueOf(tempObject.getDouble("temp"));
                    weatherInfo.setText("It is currently "+desc+" and "+ temp +"°C");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //I'm using volleyerror message to check for internet
                //This is actually used for getting weather.
                final View view = findViewById(android.R.id.content);
                Snackbar.make(view,
                        "There seems to be a problem with your internet connection",
                        Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }).show();
                driver.setClickable(false);
                user.setClickable(false);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}