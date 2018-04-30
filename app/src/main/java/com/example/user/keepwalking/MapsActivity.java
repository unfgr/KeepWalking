package com.example.user.keepwalking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static android.R.attr.color;
import static android.R.attr.duration;
import static android.R.attr.visible;
import static com.example.user.keepwalking.R.id.text;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Services vars
    private GoogleMap mMap;
    Button buttonCyc;
    Button buttonWal;
    Button buttonDis;
    Button buttonGood;
    Button buttonAvg;
    Button buttonBad;
    Button butSubmit;
    EditText commentTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    //Runtime vars
    int numOfPois = 0;
    int score = 0;
    NavEngine ne = new NavEngine();
    ArrayList<PathPoint> currentPath = new ArrayList<>();
    String username = "AnonymousUser";
    String currentMobility;
    String currentAccessibility;
    Polyline currentPolyline;
    PathPoint currentPoint;
    //App Settings
    int perPointScore = 50;
    int pathPointsDistance = 5;
    int pathLineWidth = 5;
    int pathLineColor = Color.RED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popUpEditText();
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addListenerOnButton();
        Log.d("DEBUG1","Created");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user hasasdasd
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("DEBUG1","MReady");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                Log.d("DEBUG1","Permissions OK");
            }
        }
        else {
            Log.d("DEBUG1","Mready2");
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            Log.d("DEBUG1","Version error");
        }
    }

    public void addListenerOnButton() {
        commentTxt = (EditText)findViewById(R.id.CommentText);
        buttonCyc = (Button) findViewById(R.id.but_cyclist);
        buttonWal = (Button) findViewById(R.id.but_walker);
        buttonDis = (Button) findViewById(R.id.but_diabledPerson);
        buttonGood = (Button) findViewById(R.id.button_good);
        buttonAvg = (Button) findViewById(R.id.button_average);
        buttonBad = (Button) findViewById(R.id.button_bad);
        butSubmit = (Button) findViewById(R.id.but_submit);

        butSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(numOfPois>0){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef =  database.getReference().child(username);
                    myRef = myRef.child("path-" + UUID.randomUUID().toString());
                    myRef.setValue(currentPath);
                    Toast.makeText(getApplicationContext(), "Submitted...", Toast.LENGTH_LONG).show();
                score = 0;
                numOfPois = 0;
                updatePoints(0);
                currentPath.clear();
                currentPath.add(currentPoint);
                currentPolyline.remove();

                }
                else
                    Toast.makeText(getApplicationContext(), "Nothing to Submit...", Toast.LENGTH_LONG).show();
            }

        });

        buttonWal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(mLastLocation!=null && currentPath.size()>=0){
                currentMobility = "walker";
                Toast.makeText(getApplicationContext(), "Choose Condition...", Toast.LENGTH_SHORT).show();
                enableGrading();
                } else
                    Toast.makeText(getApplicationContext(), "Err... where am I??? Enable/Wait GPS!", Toast.LENGTH_LONG).show();

            }

        });

        buttonCyc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(mLastLocation!=null && currentPath.size()>=0){
                currentMobility = "bicycle";
                Toast.makeText(getApplicationContext(), "Choose Condition...", Toast.LENGTH_SHORT).show();
                enableGrading();
            } else
                    Toast.makeText(getApplicationContext(), "Err... where am I??? Enable/Wait GPS!", Toast.LENGTH_LONG).show();

            }

        });

        buttonDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mLastLocation!=null && currentPath.size()>=0){
                currentMobility = "disabled";
                Toast.makeText(getApplicationContext(), "Choose Condition...", Toast.LENGTH_SHORT).show();
                enableGrading();
            } else
            Toast.makeText(getApplicationContext(), "Err... where am I??? Enable/Wait GPS!", Toast.LENGTH_LONG).show();

            }

        });

        buttonGood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentAccessibility = "good";
                updatePoints(1);
                Toast.makeText(getApplicationContext(), "POI added", Toast.LENGTH_SHORT).show();
                disableGrading();
                PathPoint pp = new PathPoint();
                pp.setAccessibility(currentAccessibility);
                pp.setLat(mLastLocation.getLatitude());
                pp.setLon(mLastLocation.getLatitude());
                pp.setMobility(currentMobility);
                pp.setType("POI");
                pp.setSpeed(mLastLocation.getSpeed());
                pp.setComments(commentTxt.getText().toString());
                pp.setGnssAccuracy(mLastLocation.getAccuracy());
                currentPath.remove(currentPath.size()-1);
                currentPath.add(pp);
            }

        });

        buttonAvg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentAccessibility = "average";
                updatePoints(1);
                Toast.makeText(getApplicationContext(), "POI added", Toast.LENGTH_SHORT).show();
                disableGrading();
                PathPoint pp = new PathPoint();
                pp.setAccessibility(currentAccessibility);
                pp.setLat(mLastLocation.getLatitude());
                pp.setLon(mLastLocation.getLatitude());
                pp.setMobility(currentMobility);
                pp.setType("POI");
                pp.setSpeed(mLastLocation.getSpeed());
                pp.setComments(commentTxt.getText().toString());
                pp.setGnssAccuracy(mLastLocation.getAccuracy());
                currentPath.remove(currentPath.size()-1);
                currentPath.add(pp);

            }

        });

        buttonBad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentAccessibility = "bad";
                updatePoints(1);
                Toast.makeText(getApplicationContext(), "POI added", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), ((EditText) findViewById(R.id.CommentText)).getText().toString(), Toast.LENGTH_LONG).show();
                PathPoint pp = new PathPoint();
                pp.setAccessibility(currentAccessibility);
                pp.setLat(mLastLocation.getLatitude());
                pp.setLon(mLastLocation.getLatitude());
                pp.setMobility(currentMobility);
                pp.setType("POI");
                pp.setSpeed(mLastLocation.getSpeed());
                pp.setComments(commentTxt.getText().toString());
                pp.setGnssAccuracy(mLastLocation.getAccuracy());
                currentPath.remove(currentPath.size()-1);
                currentPath.add(pp);
                disableGrading();
            }

        });


    }

    public void enableGrading(){
        commentTxt.setVisibility(View.VISIBLE);
        commentTxt.setEnabled(true);
        buttonWal.setEnabled(false);
        buttonCyc.setEnabled(false);
        buttonDis.setEnabled(false);
        butSubmit.setEnabled(false);
        buttonGood.setVisibility(View.VISIBLE);
        buttonBad.setVisibility(View.VISIBLE);
        buttonAvg.setVisibility(View.VISIBLE);
        buttonGood.setEnabled(true);
        buttonAvg.setEnabled(true);
        buttonBad.setEnabled(true);

    }

    public void disableGrading(){
        commentTxt.setVisibility(View.GONE);
        commentTxt.setEnabled(false);
        buttonWal.setEnabled(true);
        buttonCyc.setEnabled(true);
        buttonDis.setEnabled(true);
        butSubmit.setEnabled(true);
        buttonGood.setVisibility(View.GONE);
        buttonBad.setVisibility(View.GONE);
        buttonAvg.setVisibility(View.GONE);

    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("DEBUG1","Building...");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("DEBUG1","Connected");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("DEBUG1","Connectison Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        //add point to path
        PathPoint pp = new PathPoint();
        pp.setLat(location.getLatitude());
        pp.setLon(location.getLongitude());
        pp.setSpeed(location.getSpeed());
        pp.setGnssAccuracy(location.getAccuracy());
        pp.setType("normal");
        pp.setMobility("walk");
        pp.setAccessibility("good");
        currentPoint = pp;
        if(currentPath.size()<=0){
            currentPath.add(pp); //if this is the first point to path
            mLastLocation = location;
        }
        else if(mLastLocation.distanceTo(location)>=pathPointsDistance){
            currentPath.add(pp);   //second++ point as long as it is further than the preset distance from previous point
            LatLng p1 = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            LatLng p2 = new LatLng(location.getLatitude(),location.getLongitude());
            currentPolyline = mMap.addPolyline((new PolylineOptions())
                    .add(p1, p2).width(pathLineWidth).color(pathLineColor)
                    .geodesic(true));
            Log.d("DEBUG1","PolyLine");
            mLastLocation = location;  //previous point is now equal to current point [waiting for next point]
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void updatePoints(int factor){
    numOfPois = numOfPois + factor;
    score =  numOfPois*perPointScore;
    TextView t = (TextView)findViewById(R.id.scoreTxtView);
        t.setText("Score:" + score + "\nPOIs:" + numOfPois);
    }


    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Name");
        builder.setCancelable(false);

        final EditText textData = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textData.setLayoutParams(params);
        builder.setView(textData);

        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(textData.getText().toString().equals("") || textData.getText().toString().contains("\n") || textData.getText().toString().contains(" "))
                    username = "AnonymousUser";
                else
                    username = textData.getText().toString();
                Toast.makeText(getApplicationContext(), "welcome " + username, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();

    }
}

