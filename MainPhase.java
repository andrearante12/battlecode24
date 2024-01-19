package turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import battlecode.common.*;

public class MainPhase {

  private static int[] flagIDs;
    
  public static void runMainPhase(RobotController rc) throws GameActionException {

    flagIDs = new int[6];
    
    // Buy global upgrade if possible (prioritize healing and attack)
    if(rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
      rc.buyGlobal(GlobalUpgrade.HEALING);
    } 
    else if(rc.canBuyGlobal(GlobalUpgrade.ATTACK)) {
      rc.buyGlobal(GlobalUpgrade.ATTACK);
    }


    // Attack enemies, prioritizing enemies that have your flag
    RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
    for(RobotInfo robot : nearbyEnemies) {
      if(robot.hasFlag()) {
        Pathfind.bugNav2(rc, robot.getLocation());
        if(rc.canAttack(robot.getLocation())) rc.attack(robot.getLocation());
      }
    }
    for (RobotInfo robot : nearbyEnemies) {
      if (rc.canAttack(robot.getLocation())) {
        rc.attack(robot.getLocation());
      }
    }
    Communication.updateEnemyInfo(rc, rc.getLocation(), nearbyEnemies.length);
    

    // Try to heal friendly robots
    for(RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
      if(rc.canHeal(robot.getLocation())) rc.heal(robot.getLocation());
    }

    // Search for nearby enemy flags
    FlagInfo[] allFlags = rc.senseNearbyFlags(-1);
    for (FlagInfo flag : allFlags) {
      int flagID = flag.getID();
      int idx = flagIDToIdx(rc, flagID);
      Communication.updateFlagInfo(rc, flag.getLocation(), flag.isPickedUp(), 0);
    }

    // Strategy if we aren't holding the enemy flag
    if(!rc.hasFlag()) {
      Strategy.attack(rc);

      // TODO: Turtle up under some condition
      // Strategy.fortify();
    }
    else {
      // Ff we have the flag, move towards the closest ally spawn zone
      MapLocation[] spawnLocs = rc.getAllySpawnLocations();
      MapLocation closestSpawn = findClosestLocation(rc.getLocation(), Arrays.asList(spawnLocs));
      Pathfind.bugNav2(rc, closestSpawn);
    }
  }

    public static MapLocation findClosestLocation(MapLocation me, List<MapLocation> otherLocs) {
        MapLocation closest = null;
        int minDist = Integer.MAX_VALUE;
        for(MapLocation loc : otherLocs) {
            int dist = me.distanceSquaredTo(loc);
            if(dist < minDist) {
                minDist = dist;
                closest = loc;
            }
        }
        return closest;
    }

    public static int flagIDToIdx(RobotController rc, int flagID) {
      for (int i = 0; i < flagIDs.length; i++) {
        if (flagIDs[i] == 0) {
          flagIDs[i] = flagID;
          return i;
        }
        else if (flagIDs[i] == flagID) {
          return i;
        }
        else continue;
      }
      return 0;
    }
}
