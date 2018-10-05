package dstudios.ml.sdibustracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DigitalClock;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import  java.util.Date;
import java.util.List;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class layout_driver_primary extends AppCompatActivity {

    SwipeButton ride;
    private Date d;
    LocationListener locationListener;
    LocationManager locationManager;
    public double latitude;
    public double longitude;
    public String routeNumber;
    private DatabaseReference dbref;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbref = FirebaseDatabase.getInstance().getReference();
        //get route number from previous activity
        Bundle b = getIntent().getExtras();
        int routenum = b.getInt("routeno");
        routeNumber = String.valueOf(routenum);
        setContentView(R.layout.activity_layout_driver_primary);

        //declarations
        DigitalClock dc = (DigitalClock)findViewById(R.id.clock);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ride = (SwipeButton)findViewById(R.id.button_create);


        ride.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                getDriverLocation();
            }
        });
    }

    public void getDriverLocation(){

        //permission details
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }

        //not considering what happens when user disables location services.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext());

                try{
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality()+" - ";
                    //result += addresses.get(0).getCountryName()+":";
                    result += addresses.get(0).getSubLocality()+" - ";
                    result += addresses.get(0).getPostalCode();
                    TextView res = (TextView)findViewById(R.id.locId);
                    res.setText(result);

                    //add to firebase
                    dbref.child(routeNumber).child("Latitude").setValue(latitude);
                    dbref.child(routeNumber).child("Longitude").setValue(longitude);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 250, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 250, 0, locationListener);
    }
}