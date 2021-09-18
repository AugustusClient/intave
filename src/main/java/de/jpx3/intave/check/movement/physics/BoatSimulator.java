package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.player.collider.complex.ComplexColliderSimulationResult;
import de.jpx3.intave.user.User;

public final class BoatSimulator extends Simulator{
  @Override
  public ComplexColliderSimulationResult performSimulation(
    User user,
    MotionVector context,
    float keyForward, float keyStrafe,
    boolean attackReduce, boolean jumped,
    boolean handActive
  ) {


    return null;
  }

  @Override
  public void prepareNextTick(
    User user,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  ) {

  }
}