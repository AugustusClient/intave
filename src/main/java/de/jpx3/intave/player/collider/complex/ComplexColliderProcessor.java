package de.jpx3.intave.player.collider.complex;

import de.jpx3.intave.check.movement.physics.MotionVector;
import de.jpx3.intave.user.User;

public interface ComplexColliderProcessor {
  ComplexColliderSimulationResult collide(
    User user, MotionVector context,
    boolean inWeb,
    double positionX, double positionY, double positionZ
  );
}