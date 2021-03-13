package de.jpx3.intave.detect.checks.movement.physics.collider;

import de.jpx3.intave.detect.checks.movement.Physics;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaClientData;

public final class Colliders {
  private static Collider legacyCollisionResolver;
  private static Collider newCollisionResolver;

  private Colliders() {
  }

  public static void setup() {
    legacyCollisionResolver = new LegacyCollider();
    newCollisionResolver = new NewCollider();
  }

  public static SimulationResult collide(
    User user, Physics.PhysicsProcessorContext context, boolean inWeb,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaClientData clientData = user.meta().clientData();
    return clientData.applyNewEntityCollisions()
      ? newCollisionResolver.collide(user, context, inWeb, positionX, positionY, positionZ)
      : legacyCollisionResolver.collide(user, context, inWeb, positionX, positionY, positionZ);
  }
}