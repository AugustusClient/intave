package de.jpx3.intave.world.collision.access;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2021
 */

public interface OCBlockShapeAccess extends OverrideBlockShapeAccess, CachedBlockShapeAccess {

  default void applyFrom(OCBlockShapeAccess priorBlockShapeAccess) {
    indexedReplacements().clear();
    locatedReplacements().clear();
    indexedReplacements().putAll(priorBlockShapeAccess.indexedReplacements());
    locatedReplacements().putAll(priorBlockShapeAccess.locatedReplacements());
  }
}
