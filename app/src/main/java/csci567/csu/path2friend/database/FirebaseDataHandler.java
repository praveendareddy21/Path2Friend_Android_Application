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
//import csci567.csu.path2friend.googlemapspath.GoogleMapsPathActivity.DatabaseCallbackInterfaceForMap;


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

    public void add_friend(String user,  String  friend){


        final Firebase userRef = this.myFirebaseRef.child("users");
        final Firebase userfriendRef = this.myFirebaseRef.child("users").child(user).child("friends_list");

        final Firebase addFriendRef = this.myFirebaseRef.child("users").child(friend).child("friends_list");

        final String friendName = friend;
        final String userName = user;

        userRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Log.i(ACL, " key is " + dataSnapshot.getKey() + " value is " + dataSnapshot.getValue());
                if (dataSnapshot.getKey() ==friendName ) {
                    userfriendRef.child(friendName).setValue("");
                    addFriendRef.child(userName).setValue("");

                    Log.i(ACL, "pushed the friend" + friendName + " to the friends list");

                } else {
                    Log.i(ACL, "search query returns that friend is already in the friends list");
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
        Log.i(ACL, "added the friend to friend list");
    }

    public void get_friends_data(String user){



        final Firebase userfriendRef = this.myFirebaseRef.child("users").child(user);

        final String userName = user;

        userfriendRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(ACL, " key is " + dataSnapshot.getKey() + " value is " + dataSnapshot.getValue());
                if("friends_list".equals(dataSnapshot.getKey().toString())) {
                    Log.i(ACL, "in On ChildAddedd Friend List  KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());
                    HashMap<String, String> FriendList=dataSnapshot.getValue(HashMap.class);

                    if(FriendList != null) {
                        Log.i(ACL, "in On ChildAddedd Friend list found " + FriendList.keySet().toString());



                        //callback.onFriendLocationChange(userName, friendName, g);
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
        Log.i(ACL, "added the friend to friend list");
    }



    public void search_friend(UserData u){

        Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");

        // Query queryRef = ref.orderByChild("FullName").equalTo(u.getFullName());
        Log.i(ACL, "tring with  user data" + u.getFullName());
        final String userName = u.getFullName();




        ref.orderByChild(u.getFullName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                String friends = "";

                Log.i(ACL, " key is " + dataSnapshot.getKey() +" value is "+dataSnapshot.getValue());


                Log.i(ACL, "In Goddamn Child listner " + dataSnapshot.getKey() + " dinosaur's score is " + dataSnapshot.getValue());
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

    public void print_method(){

        Log.i(ACL, "just a method ");
    }

    public boolean  search_by_key(String su) {
        Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        final String userName = su;
        final boolean result =false;

        ref.orderByKey().startAt(su).endAt(su).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userName)) {
                    Log.i(ACL, "search query for " + userName + " is true ");
                    print_method();


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

        Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");

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
        Firebase ref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");


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
        Firebase setLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users");
        setLocref.child(user).child("location").setValue(g);
    }

    public void  getLocation(String user , String friend) {
        Firebase getLocref = new Firebase("https://brilliant-inferno-6550.firebaseio.com//users//"+friend);
        Log.i(ACL, "Inside getLocation for "+friend);
        final String userName= user;
        final String friendName = friend;

        getLocref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if("location".equals(dataSnapshot.getKey().toString())) {
                    Log.i(ACL, "in On ChildAddedd fot getloc KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());
                    GeoLocation g=dataSnapshot.getValue(GeoLocation.class);

                    if(g != null) {
                        Log.i(ACL, "in On ChildAddedd loc found  lat : " + g.latitude + " long : " + g.longitude);
                        //callback.onFriendLocationChange(userName, friendName, g);
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if("location".equals(dataSnapshot.getKey().toString())) {
                    Log.i(ACL, "in On Child Changed  KEY : " + dataSnapshot.getKey() + " VAL : " + dataSnapshot.getValue());
                }

                GeoLocation g=dataSnapshot.getValue(GeoLocation.class);
                if(g != null) {
                    Log.i(ACL, "in On ChildChanged lat : " + g.latitude + " long : " + g.longitude);
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


}
