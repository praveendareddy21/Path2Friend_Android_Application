package csci567.csu.path2friend.database;

import android.util.Log;

import csci567.csu.path2friend.database.UserData;


public class Model {

    final static String ACL ="Model";
    private static Long Latitude= null;
    private static String Friend=null;
    private static boolean test =false;
    private static UserData CurrentUserData;
    private static UserData CurrentFriendData;

    private static boolean isUserAuthenticated = false;

    public static synchronized  boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

    public static synchronized void setIsUserAuthenticated(boolean isUserAuthenticated) {
        isUserAuthenticated = isUserAuthenticated;
    }

    public static synchronized void setFriend(String friend) {
        Friend = friend;
    }

    public static synchronized  void setLatitude(Long latitude) {
        Latitude = latitude;
    }



    public static synchronized Long getLatitude() {
        return Latitude;
    }

    public static String getFriend() {
        return Friend;
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

    public static synchronized void setTest(boolean test) {
        test = test;
    }

    public static synchronized boolean isTest() {
        return test;
    }
    public static  synchronized void printModelState(){
        Log.i(ACL, "current User : "+getCurrentUserData().getFullName());
        Log.i(ACL, "current Friend User : "+getCurrentFriendData().getFullName());


    }
}
