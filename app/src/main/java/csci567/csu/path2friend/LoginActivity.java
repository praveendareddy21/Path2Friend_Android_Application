package csci567.csu.path2friend;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

import java.net.MalformedURLException;

public class LoginActivity extends AppCompatActivity {

    static String TAG = "Login Activity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Initializing Bluemix
        try {
            BMSClient.getInstance().initialize(getApplicationContext(),
                    "https://path2friend.mybluemix.net", "dfb91080-472f-450d-b40a-a9cfeb0bc3b7");
            Request request = new Request("/protected", Request.GET);

            request.send(this, new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    Log.d(TAG, "onSuccess :: " + response.getResponseText());
                }

                @Override
                public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                    if (null != t) {
                        Log.d(TAG, "onFailure :: " + t.getMessage());
                    } else if (null != extendedInfo) {
                        Log.d(TAG, "onFailure :: " + extendedInfo.toString());
                    } else {
                        Log.d(TAG, "onFailure :: " + response.getResponseText());
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
