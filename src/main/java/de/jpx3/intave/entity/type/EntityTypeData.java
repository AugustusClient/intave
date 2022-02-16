package de.jpx3.intave.entity.type;

import de.jpx3.intave.entity.size.HitboxSize;

import java.util.Locale;

public final class EntityTypeData {
  private final String entityName;
  private final HitboxSize hitBoxSize;
  private final int entityTypeId;
  private final boolean isLivingEntity;
  private final boolean isBoat;
  public final int creationID;

  public EntityTypeData(String entityName, HitboxSize hitBoxSize, int entityTypeId, boolean isLivingEntity, int creationID) {
    this.entityName = entityName;
    this.hitBoxSize = hitBoxSize;
    this.entityTypeId = entityTypeId;
    this.isLivingEntity = isLivingEntity;
    this.isBoat = entityName.toLowerCase(Locale.ROOT).contains("boat");
    this.creationID = creationID;
  }

  public boolean isLivingEntity() {
    return isLivingEntity;
  }

  public boolean isBoat() {
    return isBoat;
  }

  public String name() {
    return entityName;
  }

  public int identifier() {
    return entityTypeId;
  }

  public HitboxSize size() {
    return hitBoxSize;
  }
}