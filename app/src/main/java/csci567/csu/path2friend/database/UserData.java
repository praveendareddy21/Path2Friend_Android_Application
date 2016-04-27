package csci567.csu.path2friend.database;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {

    private int id;
    private String fullName;

    private Map<String , String> friends_list;
    private  GeoLocation location;

    public UserData() {}
    public UserData(String fullName, int id) {
        this.fullName = fullName;
        this.id = id;
        this.friends_list = new HashMap<String, String>();
        //this.location= new GeoLocation(31.1184944, -121.117477);
        //this.friends_list.put("default","");
        this.location= new GeoLocation(0, 0);

    }
    public long getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }


    public GeoLocation getLocation() {
        if (GeoLocation.coordinatesValid(location.latitude, location.longitude)) {
            return location;
        } else {
            return null;

        }
    }


    public Map<String, String> getFriends_list() {
        return friends_list;
    }
}
