package csci567.csu.path2friend.googlemapspath;
    import java.io.BufferedInputStream;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    import org.json.JSONObject;

    import android.Manifest;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Color;
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
    import android.view.View;
    import android.widget.Toast;

    import csci567.csu.path2friend.R;
    import csci567.csu.path2friend.database.FirebaseDataHandler;
    import csci567.csu.path2friend.database.GeoLocation;
    import csci567.csu.path2friend.database.Model;

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

public class GoogleMapsPathActivity extends FragmentActivity implements DatabaseCallbackInterfaceForMap {



    private  LatLng origin = null;
    private LatLng destination = null;
    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543, -73.998585);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);

    GoogleMap googleMap;
    final String TAG = "GoogleMapsPathActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // origin=new LatLng(Model.getUserLoc().latitude,Model.getUserLoc().longitude);
       // destination=new LatLng(Model.getFriendLoc().latitude,Model.getUserLoc().longitude);


        if(origin == null){
            origin = LOWER_MANHATTAN;
        }
        //origin = LOWER_MANHATTAN;
       // destination = WALL_STREET;
        if(destination == null){
            destination = WALL_STREET;
        }

        setContentView(R.layout.google_maps_path);
        Log.i(TAG, "In on create of GoogleMapsPathActivity");



       SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(origin);
        options.position(destination);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination,
                13));
        addMarkers();

        handlePermissionForLocation();

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        setFriendLocationChangeCallback();

    }

    public void onFriendLocationChange(String user, String friend, GeoLocation newLoc){

        Log.i(TAG, "Callback on friends location change");
        Toast.makeText(getBaseContext(),
                " Friend's Location changed : Lat: " + newLoc.getLatitude() + " Lng: "
                        + newLoc.getLongitude(), Toast.LENGTH_SHORT).show();

    }
    private void setFriendLocationChangeCallback(){
        Firebase.setAndroidContext(this.getApplicationContext());
        FirebaseDataHandler fd = new FirebaseDataHandler();

        String user=null;
        String friend = null;
        /// assign user and friend to Model Class after authentication to avoid null references
        try{
        user= Model.getCurrentUserData().getFullName();
        friend = Model.getCurrentFriendData().getFullName();
        }
        catch(NullPointerException e){
            Log.e(TAG, "Null Pointer Exception while setLocationCallback "+ e.getMessage());
        }


        Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Log.i(TAG, "Inside getLocation in Google MapPath for "+friend);
        final String userName= user;
        final String friendName = friend;

        getLocref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if("location".equals(dataSnapshot.getKey().toString())) {
                    //Log.i(TAG, "in On Child Changed  KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());


                    GeoLocation g = dataSnapshot.getValue(GeoLocation.class);
                    if (g != null) {
                        Log.i(TAG, "in On ChildChanged lat : " + g.latitude + " long : " + g.longitude);
                        onFriendLocationChange(userName, friendName, g);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }

    private String getMapsApiDirectionsUrl() {
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

    private void addMarkers() {


        if (googleMap != null) {

            googleMap.addMarker(new MarkerOptions().position(origin)
                    .title("Marker"));
            googleMap.addMarker(new MarkerOptions().position(destination)
                    .title("Third Point"));
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
            //new getData().execute("no shit");
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

    private class getData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {


            String username = args[0];
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("https://brilliant-inferno-6550.firebaseio.com/users/"+username+".json");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( IOException ioe) {

                Log.i("GetData", "IOException Error Messsage  : "+ioe.getMessage());
            }
            catch( Exception e) {
                e.printStackTrace();
                Log.i("GetData", "Error : "+e.getMessage());
            }
            finally {
                urlConnection.disconnect();
            }

            if(result.equals("")){
                Log.i("GetData", " Result is null as expected ");
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("GetData", " result from url : " + result + " length :"+result.length());
            //Do something with the JSON string

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
            }
        }

        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
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

        LocationListener locationListener = new MyLocationListener();
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

        @Override
        public void onLocationChanged(Location loc) {
            //editLocation.setText("");
            //pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }



}