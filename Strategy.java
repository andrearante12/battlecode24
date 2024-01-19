package turtle;

import java.util.ArrayList;

import battlecode.common.*;

public class Strategy {
  // Defensive Turtle Strategy
  public static void turtle(RobotController rc) throws GameActionException {
    // Bots move towards the base
    Pathfind.bugNav2(rc, new MapLocation(rc.readSharedArray(0), rc.readSharedArray(1)));
  }

  // TODO Mix in traps and water into the defensive formation
  public static void fortify(RobotController rc, int inner, int outer) throws GameActionException {

    // Move towards flags and place defenses around them
    FlagInfo[] flags = rc.senseNearbyFlags(-1);
    FlagInfo targetFlag = null;
    for(FlagInfo flag : flags) {
      if(!flag.isPickedUp()) {
        targetFlag = flag;
        break;
      }
    }

    if(targetFlag != null) {
      Pathfind.bugNav2(rc, targetFlag.getLocation());

      // Only attempt building if we are within the trap radius
      if(rc.getLocation().distanceSquaredTo(flags[0].getLocation()) < 9) {
        if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
          rc.build(TrapType.EXPLOSIVE, rc.getLocation());
        }
        else {
          MapLocation waterLoc = rc.getLocation().add(RobotPlayer.directions[RobotPlayer.random.nextInt(8)]);
          if(rc.canDig(waterLoc)) rc.dig(waterLoc);
        }
      }
    } 
    
    else {
      Pathfind.bugNav2(rc, new MapLocation(rc.readSharedArray(0), rc.readSharedArray(1)));
    } 
  }

  public static void attack(RobotController rc) throws GameActionException {
    
  }
}
