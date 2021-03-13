package de.jpx3.intave.detect.checks.movement.physics.pose;

import de.jpx3.intave.detect.checks.movement.Physics;
import de.jpx3.intave.detect.checks.movement.physics.collision.block.BlockCollisionRepository;
import de.jpx3.intave.detect.checks.movement.physics.collision.collider.Colliders;
import de.jpx3.intave.detect.checks.movement.physics.collision.collider.SimulationResult;
import de.jpx3.intave.user.User;
import de.jpx3.intave.world.waterflow.AbstractWaterflow;

public abstract class PhysicsPoseSimulator {
  private Physics physics;

  public final void checkLinkage(Physics physics) {
    this.physics = physics;
  }

  public abstract SimulationResult performSimulation(
    User user, Physics.PhysicsProcessorContext context,
    float keyForward, float keyStrafe,
    boolean attackReduce, boolean jumped, boolean handActive
  );

  public abstract void prepareNextTick(
    User user,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  );

  public Physics physics() {
    return physics;
  }

  public Colliders entityCollisionRepository() {
    return physics.entityCollisionRepository();
  }

  public AbstractWaterflow aquaticWaterMovementBase() {
    return physics.aquaticWaterMovementBase();
  }

  public BlockCollisionRepository blockCollisionRepository() {
    return physics.blockCollisionRepository();
  }

  public boolean requiresKeyCalculation() {
    return true;
  }
}