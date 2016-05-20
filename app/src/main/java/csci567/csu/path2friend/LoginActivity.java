package csci567.csu.path2friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

import csci567.csu.path2friend.googlemapspath.GoogleMapsPathActivity;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class LoginActivity extends AppCompatActivity {

    static String TAG = "Login Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ImageView bgImageView = (ImageView)findViewById(R.id.bgImageView);
        bgImageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.p2f_bg, size.x/4, size.y/4));

        //Checking if user is already signed in
        SharedPreferences sharedPreferences = getSharedPreferences("csci567.csu.path2friend",
                MODE_PRIVATE);
        if (!sharedPreferences.getString(getString(R.string.authToken), "").equals("")) {
            Log.d(TAG, "Auth Token Available");
            Log.d(TAG, sharedPreferences.getString(getString(R.string.emailID), ""));

            //Write code to load up the Map Screen here.
            Intent intent = new Intent(this, GoogleMapsPathActivity.class);
            startActivity(intent);
        }
        else {
            showTutorial();
        }
    }

    public void showTutorial() {
        Intent tutIntent = new Intent(this, MaterialTutorialActivity.class);
        tutIntent.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(tutIntent, 100);
    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem item1 = new TutorialItem("Activate SOS Mode", "Shake your phone to access SOS mode!!!",R.color.colorPrimary, R.drawable.shake_gesture, R.drawable.shake_gesture);
        TutorialItem item2 = new TutorialItem("Deactivate SOS Mode", "Tap 5 times on your screen to deactivate SOS!!!",R.color.colorPrimary, R.drawable.shake_gesture, R.drawable.shake_gesture);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(item1);
        tutorialItems.add(item2);

        return tutorialItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
