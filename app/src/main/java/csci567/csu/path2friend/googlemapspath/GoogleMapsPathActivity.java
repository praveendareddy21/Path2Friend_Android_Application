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

    import android.graphics.Color;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.Snackbar;
    import android.support.v4.app.FragmentActivity;
    import android.util.Log;
    import android.view.View;

    import csci567.csu.path2friend.R;
    import csci567.csu.path2friend.database.Model;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.PolylineOptions;

public class GoogleMapsPathActivity extends FragmentActivity {


    private  LatLng origin = null;
    private LatLng destination = null;

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

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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




}