package csci567.csu.path2friend.database;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import csci567.csu.path2friend.LoginActivityFragment.DatabaseCallbackInterface;
import csci567.csu.path2friend.database.GeoLocation;
import csci567.csu.path2friend.googlemapspath.GoogleMapsPathActivity;



import java.util.HashMap;
import java.util.Map;



public class FirebaseDataHandler {
    private final String ACL="DataBaseHandler";
    private Firebase myFirebaseRef;


    public FirebaseDataHandler(){
         myFirebaseRef = new Firebase("https://brilliant-inferno-6550.firebaseio.com");
    }


    public void save_userdata(UserData u){


        Firebase userRef = this.myFirebaseRef.child("users").child(u.getFullName());
        userRef.setValue(u);
        Log.i(ACL, " saving user data");
        }

    public void setUserAuthenticated(String su,DatabaseCallbackInterface callback ){

        Firebase userRef = this.myFirebaseRef.child("users");
        final String userName = su;
        final boolean result =false;
        final DatabaseCallbackInterface callback1= callback;

        userRef.orderByKey().startAt(su).endAt(su).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userName)) {
                    Log.i(ACL, "User " + userName + " is already authenticated.");
                    Model.setIsUserAuthenticated(true);
                    callback1.onSuccessfulUserAuthenticaion(userName);
                } else {
                    Log.i(ACL, "search query for " + userName + " has returned false");
                    callback1.onFailedUserAuthenticaion(userName);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void add_to_friendlist(String user,  String  friend){
        Log.i(ACL, " In add_to_friendlist method for add friend");
        Firebase userRef = this.myFirebaseRef.child("users").child(user).child("friends_list").child(friend);
        Firebase friendRef = this.myFirebaseRef.child("users").child(friend).child("friends_list").child(user);
        userRef.setValue("");
        friendRef.setValue("");
        Log.i(ACL," set values to userRef and friendRef");

    }

    public void add_friend(String user,  String  friend){

        Log.i(ACL,"In add friend method ");
        Firebase searchRef = this.myFirebaseRef.child("users");
        final Firebase userRef = this.myFirebaseRef.child("users").child(user).child("friends_list");
        final Firebase friendRef = this.myFirebaseRef.child("users").child(friend).child("friends_list");

        final String userName = user;
        final String friendName = friend;


        searchRef.orderByKey().startAt(friend).endAt(friend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(ACL, " key is " + dataSnapshot.getKey() + " value is " + dataSnapshot.getValue());
                if (dataSnapshot.hasChild(friendName)) {
                    Log.i(ACL, "User " + friendName + " is authenticated. So, adding to friends list");
                    add_to_friendlist(userName, friendName);


                } else {
                    Log.i(ACL, "User "+friendName+" does not exist. So, cannot add to friends list");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void get_friends_data(String user, final GoogleMapsPathActivity.getFriendListCallbackInterface callback){

        final Firebase userfriendRef = this.myFirebaseRef.child("users").child(user);
        final String userName = user;



        userfriendRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(ACL, "in On Datachange  get Friend List  KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());

                if (dataSnapshot.hasChild("friends_list")) {
                    Log.i(ACL, "inside if friends_list  On Datachange  get Friend List  KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());
                    HashMap<String, String> FriendList=dataSnapshot.child("friends_list").getValue(HashMap.class);

                    if(FriendList != null) {
                        Log.i(ACL, "in On ChildAddedd Friend list found " + FriendList.keySet().toString());
                        callback.onRetrievingFriendList(userName, FriendList.keySet());
                    }
                } else {
                    Log.i(ACL, "in On Datachange  get Friend List User " + userName + " has no friends");
                    callback.onNullFriendsList(userName);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        Log.i(ACL, "Exiting from get_friends_data ");
    }



    public void search_friend(UserData u){

       // Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        Firebase ref =this.myFirebaseRef.child("users");

        // Query queryRef = ref.orderByChild("FullName").equalTo(u.getFullName());
        Log.i(ACL, "tring with  user data" + u.getFullName());
        final String userName = u.getFullName();

        ref.orderByChild(u.getFullName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                String friends = "";

                Log.i(ACL, " key is " + dataSnapshot.getKey() +" value is "+dataSnapshot.getValue());
                if (dataSnapshot.hasChild(userName)) {
                    friends = dataSnapshot.child(userName).child("friends").getValue().toString();
                    Log.i(ACL, "friends of " + userName + " is " + friends);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        Log.i(ACL, "tried  retrirv  user data");
    }



    public boolean  search_by_key(String su) {
        // Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        Firebase ref =this.myFirebaseRef.child("users");

        final String userName = su;
        final boolean result =false;

        ref.orderByKey().startAt(su).endAt(su).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userName)) {
                    Log.i(ACL, "search query for " + userName + " is true ");

                }
                else{

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return result;

    }

    public void setFriendData (String su){

        // Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        Firebase ref =this.myFirebaseRef.child("users");

        Log.i(ACL, "tring with  user data" + su);
        final String userName = su;

        ref.orderByKey().startAt(su).endAt(su).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData result = null;
                Log.i(ACL, "Search by key for " + dataSnapshot.child(userName).getKey() + " is " + dataSnapshot.getValue());

                result = dataSnapshot.child(userName).getValue(UserData.class);

                if (result != null) {
                    Log.i(ACL, "Value : " + result.toString());
                    Model.setCurrentFriendData(result);

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    public void setUserDatatoModel (String su){
        // Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        Firebase ref =this.myFirebaseRef.child("users");

        Log.i(ACL, "tring with  user data set user data to model" + su);
        final String userName = su;



        ref.orderByKey().startAt(su).endAt(su).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData result = null;
                Log.i(ACL, "Search by key for " + dataSnapshot.child(userName).getKey() + " is " + dataSnapshot.getValue());

                result = dataSnapshot.child(userName).getValue(UserData.class);

                if (result != null) {
                    Log.i(ACL, "Value : " + result.toString());
                    Model.setCurrentUserData(result);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setLocation(String user, GeoLocation g){
        //Firebase setLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        // Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        Firebase setLocref =this.myFirebaseRef.child("users");
        setLocref.child(user).child("location").setValue(g);
    }

    public void  getLocation(String user, final GoogleMapsPathActivity.getLocationCallbackInterface callback) {
        //Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+user);
        Firebase getLocref = this.myFirebaseRef.child("users").child(user);
        Log.i(ACL, "Inside getLocation for user :  "+user);
        final String userName= user;

        getLocref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if("location".equals(dataSnapshot.getKey().toString())) {
                    Log.i(ACL, "in On ChildAddedd fot getloc KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());
                    GeoLocation g=dataSnapshot.getValue(GeoLocation.class);

                    if(g != null) {
                        Log.i(ACL, "in On ChildAddedd loc found  lat : " + g.latitude + " long : " + g.longitude);
                        callback.onRetrievingLocation(userName, g);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    public void setFriendLocationChangeCallback(String user, String friend, ChildEventListener celistner){
        //FirebaseDataHandler fd = new FirebaseDataHandler();
        //Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Firebase getLocref = this.myFirebaseRef.child("users").child(friend);
        Log.i(ACL, "Inside setFriendLocationChangeCallback  in Google MapPath for "+friend);
        final String userName= user;
        final String friendName = friend;

        ChildEventListener listener=  getLocref.addChildEventListener(celistner);

    }

    public void resetFriendLocationChangeCallback(String user, String friend, ChildEventListener celistner){
        //Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Firebase getLocref = this.myFirebaseRef.child("users").child(friend);
        Log.i(ACL, "Inside resetFriendLocationChangeCallback  in Google MapPath for "+friend);
        getLocref.removeEventListener(celistner);



    }


}
