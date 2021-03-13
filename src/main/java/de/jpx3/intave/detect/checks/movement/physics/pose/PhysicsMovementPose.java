package de.jpx3.intave.detect.checks.movement.physics.pose;

import de.jpx3.intave.detect.checks.movement.physics.pose.vehicle.PhysicsHorseMovement;

public enum PhysicsMovementPose {
  PHYSICS_NORMAL_MOVEMENT(new PhysicsNormalPlayerMovement()),
  PHYSICS_ELYTRA_MOVEMENT(new PhysicsElytraMovement()),
  PHYSICS_VEHICLE_MOVEMENT(new PhysicsHorseMovement());

  private final PhysicsPoseSimulator calculationPart;

  PhysicsMovementPose(PhysicsPoseSimulator calculationPart) {
    this.calculationPart = calculationPart;
  }

  public PhysicsPoseSimulator simulator() {
    return calculationPart;
  }
}