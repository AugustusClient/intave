package de.jpx3.intave.detect.checks.movement.physics.custom;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.Lists;
import de.jpx3.intave.adapter.ProtocolLibAdapter;
import de.jpx3.intave.tools.annotate.Nullable;
import de.jpx3.intave.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomBlocks {
  private final static MinecraftVersion MINECRAFT_VERSION = ProtocolLibAdapter.serverVersion();
  private final List<CustomBlock> blockCollisions = Lists.newArrayList();
  private final Map<Material, CustomBlock> accessCache = new HashMap<>();

  public CustomBlocks() {
    try {
      initializeBlocks();
      setupBlocks();
      prepareAccessCache();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void initializeBlocks() {
    blockCollisions.add(new CustomBlockBed());
    blockCollisions.add(new CustomBlockSlime());
    blockCollisions.add(new CustomBlockWeb());
    blockCollisions.add(new CustomBlockSoulSand());
    blockCollisions.add(new CustomBlockBerryBush());
    blockCollisions.add(new CustomBlockWeb());
  }

  private void setupBlocks() {
    for (CustomBlock blockCollision : blockCollisions) {
      blockCollision.setup(MINECRAFT_VERSION);
    }
  }

  private void prepareAccessCache() {
    for (CustomBlock blockCollision : blockCollisions) {
      if(!blockCollision.supportedOnServerVersion()) {
        continue;
      }
      for (Material material : blockCollision.materials()) {
        accessCache.put(material, blockCollision);
      }
    }
  }

  @Nullable
  public Vector entityCollision(
    User user,
    Material material,
    Location location, Location from,
    double motionX, double motionY, double motionZ
  ) {
    CustomBlock collision = findPotentialCollision(material);
    return collision != null ? collision.entityCollidedWithBlock(user, location, from, motionX, motionY, motionZ) : null;
  }

  @Nullable
  public Vector entityCollision(
    User user,
    Material material,
    double motionX, double motionY, double motionZ
  ) {
    CustomBlock collision = findPotentialCollision(material);
    return collision != null ? collision.entityCollidedWithBlock(user, motionX, motionY, motionZ) : null;
  }

  @Nullable
  public Vector blockLanded(
    User user,
    Material material,
    double motionX, double motionY, double motionZ
  ) {
    CustomBlock collision = findPotentialCollision(material);
    return collision != null ? collision.landed(user, motionX, motionY, motionZ) : null;
  }

  @Nullable
  public Vector speedFactor(
    User user,
    Material material,
    double motionX, double motionY, double motionZ
  ) {
    CustomBlock collision = findPotentialCollision(material);
    return collision != null ? collision.speedFactor(user, motionX, motionY, motionZ) : null;
  }

  public void fallenUpon(User user, Material material) {
    CustomBlock collision = findPotentialCollision(material);
    if (collision != null) {
      collision.fallenUpon(user);
    }
  }

  private CustomBlock findPotentialCollision(Material material) {
    return accessCache.get(material);
  }
}