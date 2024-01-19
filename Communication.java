package turtle;

import battlecode.common.*;

public class Communication {

    public static int LAST_FLAG_IDX = 5;
    
    public static void updateEnemyInfo(RobotController rc, MapLocation loc, int numEnemies) {

    }

    public static void updateFlagInfo(RobotController rc, MapLocation loc, boolean isCarried, int idx) {
        
    }

    // Encodes MapLocation (x, y) into a single integer by assigned an integer to 
    // correspond to each individual square on the map
    public static int locationToInt(RobotController rc, MapLocation loc) {
        if (loc == null) {
            return 0;
        }
        return 1 + loc.x + loc.y * rc.getMapWidth();
    }

    public static MapLocation intToLocation(RobotController rc, int m) {
      if (m == 0) {
        return null;
      }
      return new MapLocation((m-1) % rc.getMapWidth(), (m - 1) / rc.getMapWidth());
    }

}
