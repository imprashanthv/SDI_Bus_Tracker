package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
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
    float latitudinalPosition=0.00f, longitudinalPostion=0.00f;
    String routeNumber;
    Button changeTheme, driverCall, tiCall, driverfeedback, appfeedback;
    EditText driveredit, appedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        changeTheme=findViewById(R.id.changethemeid);
        driverCall=findViewById(R.id.drivercallid);
        tiCall=findViewById(R.id.ticallid);
        init();
        Toast.makeText(getApplicationContext(),"onCreate Called", Toast.LENGTH_SHORT);
        dbref= FirebaseDatabase.getInstance().getReference();
        Bundle b = getIntent().getExtras();
        int routeno=b.getInt("routeno");
        routeNumber = String.valueOf(routeno);
        Log.d("------routenumber-----", routeNumber);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        driverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rno = Integer.parseInt(routeNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(getResources().getStringArray(R.array.drivernumbers)[rno+1]));
                startActivity(callIntent);
            }
        });

        tiCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(getResources().getString(R.string.numti)));
                startActivity(callIntent);
            }
        });

        driveredit = findViewById(R.id.drivereditid);
        driverfeedback = findViewById(R.id.feedbackdriverid);
        driverfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driveredit.setText("");
                Toast.makeText(userActivity.this,"Thanks for submitting feedback",Toast.LENGTH_SHORT).show();
            }
        });

        appedit = findViewById(R.id.appeditid);
        appfeedback = findViewById(R.id.feedbackappid);
        appfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appedit.setText("");
                Toast.makeText(userActivity.this,"Thanks for submitting feedback",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingUpPanelLayoutId);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        Toast.makeText(getApplicationContext(),"onMapReady called", Toast.LENGTH_SHORT);
        mMap = googleMap;
        /*Idea here is that... when data change is triggered.. we change the location of marker on the map*/
        //default location is sheriguda
        changeTheme.setOnClickListener(new View.OnClickListener() {
            int i=0;
            @Override
            public void onClick(View view) {
                if(i==5){
                    i=0;
                }
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    mMap = googleMap;

                    int[] a={R.raw.mapstyle1,R.raw.mapstyle2,R.raw.mapstyle3,R.raw.mapstyle4,R.raw.mapstyle5};

                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(getApplicationContext(),a[i]));

                    if (!success) {
                        Log.e("", "Style parsing failed.");
                    }
                    if(success){
                        i++;
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("", "Can't find style. Error: ", e);
                }
            }
        });
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
                mMap.addMarker(new MarkerOptions().position(busLocation).title("Your bus is here").icon(getBitmapDescriptor(R.drawable.busmarker)));
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(R.drawable.busmarker);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }
}
