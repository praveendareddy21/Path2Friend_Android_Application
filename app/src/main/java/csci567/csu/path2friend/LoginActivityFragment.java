package csci567.csu.path2friend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;
import com.ibm.mobilefirstplatform.clientsdk.android.security.googleauthentication.GoogleAuthenticationManager;

import org.json.JSONObject;

import java.net.MalformedURLException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    static String TAG = "Login Activity Fragment:";
    final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.sign_in_button)
    public void loginButtonPressed() {
        //Initializing Bluemix
        Log.d(TAG, "Button Clicked");
        try {
            BMSClient.getInstance().initialize(getActivity().getApplicationContext(),
                    "https://path2friend.mybluemix.net", "dfb91080-472f-450d-b40a-a9cfeb0bc3b7");

            Log.d(TAG, "In BMS Client");
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.GET_ACCOUNTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.GET_ACCOUNTS)) {

                    Toast.makeText(getActivity(), "Please give us permission to access your account to login", Toast.LENGTH_LONG).show();

                } else {

                    requestPermissions(
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GoogleAuthenticationManager.getInstance().register(getActivity().getApplicationContext());
                    Request request = new Request("/protected", Request.GET);

                    request.send(getActivity(), new ResponseListener() {

                        @Override
                        public void onSuccess(Response response) {
                            Log.d(TAG, "onSuccess :: " + response.getResponseText());
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("csci567.csu.path2friend", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.authToken), AuthorizationManager.getInstance().getUserIdentity().getId());
                            editor.putString(getString(R.string.emailID), AuthorizationManager.getInstance().getUserIdentity().getDisplayName());
                            editor.commit();
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
}
