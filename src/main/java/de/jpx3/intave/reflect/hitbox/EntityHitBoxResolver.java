package de.jpx3.intave.reflect.hitbox;

import org.bukkit.entity.Entity;

public interface EntityHitBoxResolver {
  HitBoxBoundaries hitBoxOf(Entity entity);
  HitBoxBoundaries hitBoxOf(Object serverEntity);
}