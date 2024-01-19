package turtle;

import battlecode.common.*;

public class Setup {

    private static final int EXPLORE_ROUNDS = 150;
    private static boolean foundCorner = false;
    private static boolean plantedFlag = false;

    public static void runSetup(RobotController rc) throws GameActionException {
      
        if (foundCorner == false) {
          rc.writeSharedArray(3, 0);
          rc.writeSharedArray(4, 0);
          rc.writeSharedArray(5, 0);
          foundCorner = true;
        }  

        if(rc.getRoundNum() < EXPLORE_ROUNDS) {
          rc.setIndicatorString(Integer.toString(rc.getRoundNum()));

            // Try to pick up a nearby flag
            FlagInfo[] flags = rc.senseNearbyFlags(-1);
            for(FlagInfo flag : flags) {
                MapLocation flagLoc = flag.getLocation();
                if(rc.senseMapInfo(flagLoc).isSpawnZone() && rc.canPickupFlag(flagLoc)) {
                    rc.pickupFlag(flag.getLocation());
                }
            }
            
            // If we don't have a flag, explore randomly (search for bread crumbs)
            if (!rc.hasFlag()){
                Pathfind.explore(rc);
                
                // Compute the distance to the center of the map
                MapLocation center = new MapLocation(rc.getMapWidth()/2,rc.getMapHeight()/2);
                MapLocation currentLoc = new MapLocation(rc.getLocation().x, rc.getLocation().y);
                int distFromCenter = center.distanceSquaredTo(currentLoc);

                // Save locations that are far away from the center (corner to place flag in)
                if(distFromCenter > rc.readSharedArray(3)) {
                    rc.writeSharedArray(3, distFromCenter);
                    rc.writeSharedArray(4, currentLoc.x);
                    rc.writeSharedArray(5, currentLoc.y);
                }
            }

            // If we do have a flag, try to bring it to a corner
            else {
              Pathfind.findCorner(rc);
            }

        }

        // During turns 150 - 200, try and place a flag (that has hopefully been brought to a corner)
        else {
          //try to place flag if it is far enough away from other flags
          if(rc.hasFlag() && rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
            if(rc.canDropFlag(rc.getLocation())) {
              rc.dropFlag(rc.getLocation());
              // Write the location of the flag to shared array (rally point)
              if (!plantedFlag) {
                rc.writeSharedArray(0, rc.getLocation().x);
                rc.writeSharedArray(1, rc.getLocation().y);
                plantedFlag = true;
              }
            }
          } else {
            // If we can't find a spot to place flag, move away from the nearest flag
            if (plantedFlag && rc.hasFlag()) Pathfind.moveAwayFromFlag(rc);

            // If we don't have a flag, move to lineup position
            Pathfind.lineup(rc);

            // Strategy.fortify();
          }

        }
    }
}
