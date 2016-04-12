package csci567.csu.path2friend.database;



import android.util.Log;
import csci567.csu.path2friend.database.GeoLocation;


public class Model {

    final static String ACL ="Model";

    private static boolean isUserAuthenticated = false;
    private static UserData CurrentUserData;
    private static UserData CurrentFriendData;

    private  static GeoLocation UserLoc = null;
    private  static GeoLocation FriendLoc = null;

    public static synchronized void setUserLoc(GeoLocation userLoc) {
        UserLoc = userLoc;
    }

    public static synchronized GeoLocation getUserLoc() {
        return UserLoc;
    }

    public static synchronized void setFriendLoc(GeoLocation friendLoc) {
        FriendLoc = friendLoc;
    }

    public static synchronized GeoLocation getFriendLoc() {
        return FriendLoc;
    }


    public static synchronized  boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

    public static synchronized void setIsUserAuthenticated(boolean isUserAuthenticated) {
        isUserAuthenticated = isUserAuthenticated;
    }


    public static synchronized void setCurrentUserData(UserData currentUserData) {
        CurrentUserData = currentUserData;
    }

    public static synchronized UserData getCurrentUserData() {
        return CurrentUserData;
    }

    public static synchronized void setCurrentFriendData(UserData currentFriendData) {
        CurrentFriendData = currentFriendData;
    }

    public static UserData getCurrentFriendData() {
        return CurrentFriendData;
    }


    public static  synchronized void printModelState(){
        if(getCurrentUserData()!= null)
            Log.i(ACL, "current User : "+getCurrentUserData().getFullName());
        if(getCurrentFriendData()!= null)
            Log.i(ACL, "current Friend User : "+getCurrentFriendData().getFullName());

    }
}
