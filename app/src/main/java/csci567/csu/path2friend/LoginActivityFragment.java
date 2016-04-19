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
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import butterknife.ButterKnife;
import butterknife.OnClick;
import csci567.csu.path2friend.database.FirebaseDataHandler;
import csci567.csu.path2friend.database.Model;
import csci567.csu.path2friend.database.UserData;
import csci567.csu.path2friend.googlemapspath.GoogleMapsPathActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    static String TAG = "Login Activity Fragment:";
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInOptions gso;
    GoogleApiClient apiClient;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public interface DatabaseCallbackInterface {

        void onSuccessfulUserAuthenticaion(String user);
        void onFailedUserAuthenticaion(String user);


    }

    final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(), this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        return rootView;
    }

    @OnClick(R.id.sign_in_button)
    public void loginButtonPressed() {
        Log.d(TAG, "Button Clicked");
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    }

                else {
                    Toast.makeText(getActivity(), "Please give us permission to access your account to login", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    void checkIfUserExists(final String authToken, final String emailID) {

        Firebase.setAndroidContext(getActivity().getApplicationContext());
        final FirebaseDataHandler fd = new FirebaseDataHandler();

        fd.setUserAuthenticated(emailID.replace('.', ','), new DatabaseCallbackInterface() {
            @Override
            public void onSuccessfulUserAuthenticaion(String user) {
                Log.i(TAG, "Callback on successful user authentication for user " + user);
                // code to continue after authentication
                fd.setUserDatatoModel(user);
                loadMapActivity();

            }
            public void onFailedUserAuthenticaion(String user){
                Log.i(TAG, "Callback on failed user authentication for user "+user);
                insertUser(authToken, emailID);
                loadMapActivity();
            }
        });
    }

    void loadMapActivity() {
        Intent intent = new Intent(getActivity(), GoogleMapsPathActivity.class);
        startActivity(intent);
    }

    void insertUser(String authToken, String emailID) {

        emailID = emailID.replace('.', ',');
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        FirebaseDataHandler fd = new FirebaseDataHandler();

        UserData userData = new UserData(emailID, 1);

        fd.save_userdata(userData);
        Model.setCurrentUserData(userData);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, acct.getEmail());

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("csci567.csu.path2friend", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.authToken), acct.getId());
            editor.putString(getString(R.string.emailID), acct.getEmail());
            editor.commit();

            checkIfUserExists(acct.getId(),acct.getEmail());

        } else {
            // Signed out, show unauthenticated UI.
        }
    }
}
