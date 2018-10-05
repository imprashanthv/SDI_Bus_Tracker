package dstudios.ml.sdibustracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SlidingUpPanelLayout mLayout;

    private GoogleMap mMap;
    private DatabaseReference dbref;
    float latitudinalPosition=0.00f;
    float longitudinalPostion=0.00f;
    Marker marker;
    String routeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        Toast.makeText(getApplicationContext(),"onCreate Called", Toast.LENGTH_SHORT);
        dbref= FirebaseDatabase.getInstance().getReference(); //created reference
        Bundle b = getIntent().getExtras();
        int routeno=b.getInt("routeno");
        routeNumber = String.valueOf(routeno);
        Log.d("------routenumber-----", routeNumber);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void init(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingUpPanelLayoutId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(),"onMapReady called", Toast.LENGTH_SHORT);
        mMap = googleMap;
        /*Idea here is that... when data change is triggered.. we change the location of marker on the map*/
        //default sheriguda fixed
        LatLng busLocation  = new LatLng(17.2102,78.6214);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(busLocation, 16);
        //removing old marker... so that you won't see 100s of markers on single map
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(busLocation).title("Your bus is here"));
        mMap.animateCamera(cameraUpdate);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    latitudinalPosition = dataSnapshot.child(routeNumber).child("Latitude").getValue(Float.class);
                    longitudinalPostion = dataSnapshot.child(routeNumber).child("Longitude").getValue(Float.class);
                    Log.d("latitude", String.valueOf(latitudinalPosition));
                    Log.d("longitude", String.valueOf(longitudinalPostion));
                }
                catch(DatabaseException de){
                    Toast.makeText(userActivity.this,
                            "Selected route number was never added to database",
                            Toast.LENGTH_LONG).show();
                }
                //Putting it on Google maps
                LatLng busLocation  = new LatLng(latitudinalPosition, longitudinalPostion);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(busLocation, 16);
                //removing old marker... so that you won't see 100s of markers on single map
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(busLocation).title("Your bus is here"));
                mMap.animateCamera(cameraUpdate);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
