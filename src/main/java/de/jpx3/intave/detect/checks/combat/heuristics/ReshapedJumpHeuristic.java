package de.jpx3.intave.detect.checks.combat.heuristics;

import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.tools.client.SinusCache;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class ReshapedJumpHeuristic extends IntaveCheck {
  public ReshapedJumpHeuristic(String checkName, String configurationName) {
    super(checkName, configurationName);
  }

  private void checkInvalidJump(Player player) {
    User user = userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    boolean jump = Math.abs(movementData.jumpUpwardsMotion() - movementData.motionY()) < 1e-5;

    if (jump && movementData.sprinting && movementData.suspiciousMovement) {
      float rotationYaw = movementData.rotationYaw;
      float yawSine = SinusCache.sin(rotationYaw * (float) Math.PI / 180.0F, false);
      float yawCosine = SinusCache.cos(rotationYaw * (float) Math.PI / 180.0F, false);

      Vector motion = new Vector(movementData.physicsLastMotionX, 0.0, movementData.physicsLastMotionZ);
      float friction = 0.13f;
      float moveForward = movementData.keyForward * 0.98f;
      float moveStrafe = movementData.keyStrafe * 0.98f;

      physicsCalculateRelativeMovement(motion, friction, yawSine, yawCosine, moveForward, moveStrafe);
      double distance = Math.hypot(motion.getX() - movementData.motionX(), motion.getZ() - movementData.motionZ());
      double abs = Math.abs(distance - 0.2);
      if (abs < 1e-5) {
        //TODO: Flag
      }
    }
  }

  private void physicsCalculateRelativeMovement(
    Vector motion, float friction,
    float yawSine, float yawCosine,
    float moveForward, float moveStrafe
  ) {
    float f = moveStrafe * moveStrafe + moveForward * moveForward;
    if (f >= 1.0E-4F) {
      f = (float) Math.sqrt(f);
      f = friction / Math.max(1.0f, f);
      moveStrafe *= f;
      moveForward *= f;
      motion.setX(motion.getX() + (moveStrafe * yawCosine - moveForward * yawSine));
      motion.setZ(motion.getZ() + (moveForward * yawCosine + moveStrafe * yawSine));
    }
  }
}