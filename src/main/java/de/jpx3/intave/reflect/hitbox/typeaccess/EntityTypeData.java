package de.jpx3.intave.reflect.hitbox.typeaccess;

import de.jpx3.intave.reflect.hitbox.HitBoxBoundaries;

public final class EntityTypeData {
  private final String entityName;
  private final HitBoxBoundaries hitBoxBoundaries;
  private final int entityTypeId;

  public EntityTypeData(String entityName, HitBoxBoundaries hitBoxBoundaries, int entityTypeId) {
    this.entityName = entityName;
    this.hitBoxBoundaries = hitBoxBoundaries;
    this.entityTypeId = entityTypeId;
  }

  public String entityName() {
    return entityName;
  }

  public int entityTypeId() {
    return entityTypeId;
  }

  public HitBoxBoundaries hitBoxBoundaries() {
    return hitBoxBoundaries;
  }
}