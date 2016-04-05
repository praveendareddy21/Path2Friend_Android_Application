package csci567.csu.path2friend.database;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {

    private int id;
    private String fullName;
    private String  friends;
    private int change;
    private Map<String , String> friends_list;

    public UserData() {}
    public UserData(String fullName, int id) {
        this.fullName = fullName;
        this.id = id;
        this.friends="";
        this.change=0;
        this.friends_list = new HashMap<String, String>();

        this.friends_list.put("default","");

    }
    public long getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public String getFriends() {return friends;}
    public int getChange(){return change;}

    public Map<String, String> getFriends_list() {
        return friends_list;
    }
}
