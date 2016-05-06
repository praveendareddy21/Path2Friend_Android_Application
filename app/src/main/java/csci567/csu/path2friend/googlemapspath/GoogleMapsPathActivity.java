package csci567.csu.path2friend.googlemapspath;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Set;
    import org.json.JSONObject;
    import android.Manifest;
    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.graphics.Color;
    import android.hardware.SensorManager;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.Snackbar;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.content.ContextCompat;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.NumberPicker;
    import android.widget.Toast;

    import csci567.csu.path2friend.R;
    import csci567.csu.path2friend.database.FirebaseDataHandler;
    import csci567.csu.path2friend.database.GeoLocation;
    import csci567.csu.path2friend.database.Model;
    import csci567.csu.path2friend.database.UserData;

    import com.daimajia.androidanimations.library.Techniques;
    import com.daimajia.androidanimations.library.YoYo;
    import com.firebase.client.ChildEventListener;
    import com.firebase.client.DataSnapshot;
    import com.firebase.client.Firebase;
    import com.firebase.client.FirebaseError;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.PolylineOptions;
    import com.nineoldandroids.animation.Animator;
    import com.squareup.seismic.ShakeDetector;
    import com.firebase.geofire.util.GeoUtils;



public class GoogleMapsPathActivity extends FragmentActivity implements ShakeDetector.Listener {

    public interface getFriendListCallbackInterface {
        void onRetrievingFriendList(String user, Set<String> friendList);
    }
    public interface getLocationCallbackInterface {
        void onRetrievingLocation(String user, GeoLocation g);
    }

    private  LatLng origin = null;
    private LatLng destination = null;
    private String _user = null;
    private String _friend= null;
    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private FirebaseDataHandler _fd= null;

    private ChildEventListener _friend_loc_listener = null;

    GoogleMap googleMap;
    final String TAG = "GoogleMapsPathActivity";
    View sosView;

    public void callNumberPicker(String [] friends){
        Log.i(TAG, "Starting Number Picker for selecting friend out of friends list");
        int options= friends.length;
        final String [] friends_list= friends;
        final Dialog d = new Dialog(GoogleMapsPathActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(options-1);
        np.setDisplayedValues( friends );

        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i(TAG, " Selected number is "+newVal);
            }
        });
        b1.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) {
                Log.i(TAG, " Value set as "+np.getValue());
                String earlier_friend=_friend;
                _friend=friends_list[np.getValue()];

                if(_friend != null && !_friend.equals(earlier_friend) && _friend_loc_listener != null){
                    resetFriendLocationChangeCallback(_user, earlier_friend);
                }
                setFriendLocationCallback();
                setFriendLocationChangeCallback(_user , _friend);
                d.dismiss();
            }});

        b2.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) {d.dismiss();}});d.show();}

    private void  refreshMap(){
        googleMap.clear();
        String url = getMapsApiDirectionsUrl();
        if (url != null) {
            Log.i(TAG, " url in refresh Map is "+ url);
            double newDist = GeoUtils.distance(origin.latitude, origin.longitude, destination.latitude, destination.longitude);

            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
            MarkerOptions options = new MarkerOptions();
            options.position(origin);
            options.position(destination);
            googleMap.addMarker(options);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
            addMarkers();
            Toast.makeText(this, "Updated distance between you and your friend is "+newDist +" metres.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Google Map refreshed.");
        }
    }

    private int tapCounter = 1;
    Button share_loc_button;
    FloatingActionButton fab1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_maps_path);
        SharedPreferences sharedPreferences = getSharedPreferences("csci567.csu.path2friend",
                MODE_PRIVATE);
        this._user= sharedPreferences.getString(getString(R.string.emailID), "").replace('.', ',');

         sosView = findViewById(R.id.sosView);

        Firebase.setAndroidContext(this.getApplicationContext());
        _fd = new FirebaseDataHandler();

        setUserLocationCallback();

        share_loc_button= (Button)findViewById(R.id.startSharingButton);
        share_loc_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getUsersFriendlist(_user);
            }
        });



        Log.i(TAG, "In on create of GoogleMapsPathActivity");



       SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap();

        handlePermissionForLocation();

        fab1 = (FloatingActionButton) findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View addFriendAlert = li.inflate(R.layout.add_friend_alert_dialog, null);

        AlertDialog.Builder alertDialogBuiler = new AlertDialog.Builder(GoogleMapsPathActivity.this);
        alertDialogBuiler.setView(addFriendAlert);

        final EditText userInput = (EditText) addFriendAlert.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuiler.setCancelable(false).setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String friendEmail = userInput.getText().toString();
                Log.d(TAG, "Add friend: " + friendEmail);

                if (friendEmail.length() == 0) {
                    Toast.makeText(getBaseContext(),
                            "Please enter a proper email address", Toast.LENGTH_SHORT).show();
                }

                _fd.add_friend(_user ,friendEmail.replace('.', ','));

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuiler.create();
        alert.show();

        }
    });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        sosView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (tapCounter % 5 == 0) {

                    YoYo.with(Techniques.BounceInUp).duration(800).withListener(new Animator.AnimatorListener(){
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            sosView.setVisibility(View.GONE);
                            share_loc_button.setEnabled(true);
                            fab1.setEnabled(true);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(sosView);
                    tapCounter++;
                }
                else {
                    tapCounter ++;
                }
            }
        });

    }

//    boolean sosActivated = false;
    public void hearShake() {
        if (sosView.getVisibility() == View.GONE) {
//            sosView.setVisibility(View.VISIBLE);

            //Send SMS every 5 minutes
            YoYo.with(Techniques.BounceInDown).duration(800).withListener(new Animator.AnimatorListener(){
                @Override
                public void onAnimationStart(Animator animation) {
                    sosView.setVisibility(View.VISIBLE);
                    share_loc_button.setEnabled(false);
                    fab1.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(sosView);
        }
//        Toast.makeText(this, "Don't shake me, bro!", Toast.LENGTH_SHORT).show();
    }
    
    private void getUsersFriendlist(String user){
       _fd.get_friends_data(user, new getFriendListCallbackInterface() {
            @Override
            public void onRetrievingFriendList(String user, Set<String> friendList) {
                String[] friends = friendList.toArray(new String[friendList.size()]);
                callNumberPicker(friends);
            }
        });

    }

    public void onFriendLocationChange(String user, String friend, GeoLocation newLoc){

        Log.i(TAG, "Callback on friends location change");
        Toast.makeText(getBaseContext(),
                " Friend's Location changed : Lat: " + newLoc.getLatitude() + " Lng: "
                        + newLoc.getLongitude(), Toast.LENGTH_SHORT).show();

        LatLng friendLocation = new LatLng(newLoc.getLatitude(), newLoc.getLongitude());
        destination = friendLocation;
        refreshMap();
    }

       private void setFriendLocationChangeCallback(String user, String friend){
        FirebaseDataHandler fd = new FirebaseDataHandler();
        Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Log.i(TAG, "Inside setFriendLocationChangeCallback  in Google MapPath for "+friend);
        final String userName= user;
        final String friendName = friend;

        ChildEventListener listener=  getLocref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if("location".equals(dataSnapshot.getKey().toString())) {
                    GeoLocation g = dataSnapshot.getValue(GeoLocation.class);
                    if (g != null) {
                        Log.i(TAG, "in On ChildChanged lat : " + g.latitude + " long : " + g.longitude);
                        onFriendLocationChange(userName, friendName, g);
                    }
                }
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(FirebaseError firebaseError) {}}

        );
        _friend_loc_listener = listener;

    }

    private void resetFriendLocationChangeCallback(String user, String friend){
        FirebaseDataHandler fd = new FirebaseDataHandler();
        Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Log.i(TAG, "Inside resetFriendLocationChangeCallback  in Google MapPath for "+friend);

        if(_friend_loc_listener != null) {
            getLocref.removeEventListener(_friend_loc_listener);
            Log.i(TAG, " Removed EventListener for Friend location change callback");
        }


    }

    private String getMapsApiDirectionsUrl() {

        if (origin != null && destination != null) {
            String waypoints = "waypoints=optimize:true|"
                    + origin.latitude + "," + origin.longitude
                    + "|" + "|" + destination.latitude + ","
                    + destination.longitude;


            String OriDest = "origin="+origin.latitude+","+origin.longitude+"&destination="+destination.latitude+","+destination.longitude;
            String sensor = "sensor=false";
            String params = OriDest+"&%20"+ waypoints + "&" + sensor;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + params;
            return url;
        }
        return null;
    }
    private void setUserLocationCallback(){
        Log.d(TAG, "Inside setUserLocationCallback ");
        _fd.getLocation(_user, new getLocationCallbackInterface() {
            @Override
            public void onRetrievingLocation(String user, GeoLocation g) {
                if(g.getLatitude() ==0.0 && g.getLongitude() == 0.0 ){
                    return;
                }
                else{
                    origin=new LatLng(g.latitude,g.longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
//                googleMap.setMyLocationEnabled(true);
                }
            }
        });
    }
    private void setFriendLocationCallback(){
        Log.d(TAG, "Inside setFriendLocationCallback ");
        if(_friend == null)
            return;
        _fd.getLocation(_friend, new getLocationCallbackInterface() {
            @Override
            public void onRetrievingLocation(String user, GeoLocation g) {
                if(g.getLatitude()==0.0 || g.getLongitude() == 0.0){
                    Log.e(TAG, " User "+_friend +" location has not been set up");
                    //googleMap.clear();
                    return;
                }
                destination=new LatLng(g.latitude,g.longitude);
                refreshMap();
            }
        });
    }

    private void addMarkers() {


        if (googleMap != null) {

            googleMap.addMarker(new MarkerOptions().position(origin)
                    .title("origin"));
            googleMap.addMarker(new MarkerOptions().position(destination)
                    .title("destination"));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }



    void handlePermissionForLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Please give us permission to access your location. We need this to allow you and your friend to track each other", Toast.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                //write code to fetch location here.
                if(origin != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
            }
        }

        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationTracking();
                    googleMap.setMyLocationEnabled(true);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void startLocationTracking() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        LocationListener locationListener = new MyLocationListener(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 500, 10, locationListener);
    }
    private class MyLocationListener implements LocationListener {

        private Context context;
        MyLocationListener(Context c) {
            context=c;
        }

        @Override
        public void onLocationChanged(Location loc) {
            //editLocation.setText("");
            //pb.setVisibility(View.INVISIBLE);

            Toast.makeText(getBaseContext(), "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);

            LatLng currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            _fd.setLocation(_user, new GeoLocation(loc.getLatitude(), loc.getLongitude()));
            origin = currentLocation;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

            refreshMap();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}