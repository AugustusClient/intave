package de.jpx3.intave.detect.checks.movement.physics.collision.collider;

import de.jpx3.intave.detect.checks.movement.Physics;
import de.jpx3.intave.detect.checks.movement.physics.collision.PhysicsEntityCollision;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaClientData;

public final class Colliders {
  private PhysicsEntityCollision legacyCollisionResolver;
  private PhysicsEntityCollision newCollisionResolver;

  public Colliders() {
    setup();
  }

  private void setup() {
    legacyCollisionResolver = new LegacyCollider();
    newCollisionResolver = new NewCollider();
  }

  public SimulationResult resolveEntityCollisionOf(
    User user, Physics.PhysicsProcessorContext context, boolean inWeb,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaClientData clientData = user.meta().clientData();
    return clientData.applyNewEntityCollisions()
      ? newCollisionResolver.resolveCollision(user, context, inWeb, positionX, positionY, positionZ)
      : legacyCollisionResolver.resolveCollision(user, context, inWeb, positionX, positionY, positionZ);
  }
}