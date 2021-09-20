package de.jpx3.intave.player.collider.complex;

import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.shade.Motion;

public final class ComplexColliderSimulationResult {
  private final static ComplexColliderSimulationResult INVALID_SIMULATION = new ComplexColliderSimulationResult(
    new Motion(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), false, false, false, false, false, false
  );

  private final Motion context;
  private final boolean onGround, collidedHorizontally, collidedVertically;
  private final boolean resetMotionX, resetMotionZ;
  private final boolean step;

  public ComplexColliderSimulationResult(
    Motion context, boolean onGround,
    boolean collidedHorizontally, boolean collidedVertically,
    boolean resetMotionX, boolean resetMotionZ, boolean step
  ) {
    if (context == null) {
      throw new IllegalArgumentException("Context cannot be null");
    }
    this.context = context;
    this.onGround = onGround;
    this.collidedHorizontally = collidedHorizontally;
    this.collidedVertically = collidedVertically;
    this.resetMotionX = resetMotionX;
    this.resetMotionZ = resetMotionZ;
    this.step = step;
  }

  public double accuracy(Motion motionVector) {
    return MathHelper.distanceOf(context, motionVector);
  }

  public Motion motion() {
    return context;
  }

  public boolean onGround() {
    return onGround;
  }

  public boolean collidedHorizontally() {
    return collidedHorizontally;
  }

  public boolean collidedVertically() {
    return collidedVertically;
  }

  public boolean step() {
    return step;
  }

  public boolean resetMotionX() {
    return resetMotionX;
  }

  public boolean resetMotionZ() {
    return resetMotionZ;
  }

  public static ComplexColliderSimulationResult invalid() {
    return INVALID_SIMULATION;
  }
}