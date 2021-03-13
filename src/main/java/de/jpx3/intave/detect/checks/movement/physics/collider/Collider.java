package de.jpx3.intave.detect.checks.movement.physics.collider;

import de.jpx3.intave.detect.checks.movement.Physics;
import de.jpx3.intave.user.User;

public interface Collider {
  float STEP_HEIGHT = 0.6f;

  SimulationResult collide(
    User user, Physics.PhysicsProcessorContext context,
    boolean inWeb,
    double positionX, double positionY, double positionZ
  );
}