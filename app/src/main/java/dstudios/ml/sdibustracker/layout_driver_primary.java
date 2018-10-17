package dstudios.ml.sdibustracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import  java.util.Date;
import java.util.List;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class layout_driver_primary extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    SwipeButton ride;
    private Date d;
    LocationListener locationListener;
    LocationManager locationManager;
    public double latitude;
    public double longitude;
    public String routeNumber;
    int routenum;
    private DatabaseReference dbref;
    Chronometer cn;
    boolean isChronometerRunning = false;
    long timeWhenStopped = 0;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;

    //TODO: add route cancelled functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbref = FirebaseDatabase.getInstance().getReference();
        //get route number from previous activity
        Bundle b = getIntent().getExtras();
        routenum = b.getInt("routeno");
        routeNumber = String.valueOf(routenum);
        setContentView(R.layout.activity_layout_driver_primary);

        //declarations
        DigitalClock dc = (DigitalClock)findViewById(R.id.clock);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ride = (SwipeButton)findViewById(R.id.button_create);

        //Location settings
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        ride.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                getDriverLocation();
                cn=findViewById(R.id.chronometerid);
                if(!isChronometerRunning) {
                    cn.setBase(SystemClock.elapsedRealtime()
                            +timeWhenStopped);
                    cn.start();
                    isChronometerRunning=!isChronometerRunning;
                    Toast.makeText(getApplicationContext(),"Ride started", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().goOnline();
                }
                else{
                    cn.stop();
                    isChronometerRunning=!isChronometerRunning;
                    timeWhenStopped = cn.getBase() - SystemClock.elapsedRealtime();
                    Toast.makeText(getApplicationContext(),"Ride stopped", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().goOffline();
                }
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try{
                        List<Address> addresses =
                                geocoder.getFromLocation(latitude, longitude, 1);
                        String result = addresses.get(0).getSubLocality()+" - ";
                        //result += addresses.get(0).getCountryName()+":";
                        result += addresses.get(0).getLocality();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    layout_driver_primary.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        final View view = findViewById(android.R.id.content);
                        ride.setVisibility(View.INVISIBLE);
                        Snackbar.make(view,
                                "You need to Enable Location Services!",
                                Snackbar.LENGTH_INDEFINITE).setAction("okay", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), layout_driver_primary.class);
                                intent.putExtra("routeno",routenum);
                                finish();
                                startActivity(intent);
                            }
                        }).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


