package csci567.csu.path2friend.googlemapspath;

import csci567.csu.path2friend.database.GeoLocation;

/**
 * Created by praveen on 4/12/2016.
*/

public interface DatabaseCallbackInterfaceForMap {

    void onFriendLocationChange(String user, String friend, GeoLocation newLoc);

}