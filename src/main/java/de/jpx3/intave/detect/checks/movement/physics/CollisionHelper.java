package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.tools.wrapper.WrappedBlockPosition;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.world.BlockAccessor;
import de.jpx3.intave.world.collision.CollisionFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public final class CollisionHelper {
  private final static float PLAYER_HEIGHT = 1.8f;
  private final static double HALF_WIDTH = 0.3;

  public static WrappedAxisAlignedBB entityBoundingBoxOf(
    User user,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaMovementData movementData = user.meta().movementData();
    double width = movementData.width / 2.0;
    float height = movementData.height;
    return new WrappedAxisAlignedBB(
      positionX - width, positionY, positionZ - width,
      positionX + width, positionY + height, positionZ + width
    );
  }

  public static WrappedAxisAlignedBB entityBoundingBoxOf(Location center) {
    return entityBoundingBoxOf(center.getX(), center.getY(), center.getZ());
  }

  public static WrappedAxisAlignedBB entityBoundingBoxOf(WrappedBlockPosition position) {
    return entityBoundingBoxOf(position.xCoord, position.yCoord, position.zCoord);
  }

  public static WrappedAxisAlignedBB entityBoundingBoxOf(
    double positionX, double positionY, double positionZ
  ) {
    return new WrappedAxisAlignedBB(
      positionX - HALF_WIDTH, positionY, positionZ - HALF_WIDTH,
      positionX + HALF_WIDTH, positionY + PLAYER_HEIGHT, positionZ + HALF_WIDTH
    );
  }

  public static CollisionResult resolveQuickCollisions(
    Player player,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  ) {
    WrappedAxisAlignedBB boundingBox = CollisionHelper.entityBoundingBoxOf(positionX, positionY, positionZ);
    List<WrappedAxisAlignedBB> collisionBoxes = CollisionFactory.getCollisionBoxes(
      player,
      boundingBox.addCoord(motionX, motionY, motionZ)
    );
    double startMotionY = motionY;
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionY = collisionBox.calculateYOffset(boundingBox, motionY);
    }
    boundingBox = (boundingBox.offset(0.0D, motionY, 0.0D));
    boolean onGround = startMotionY != motionY && startMotionY < 0.0D;
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionX = collisionBox.calculateXOffset(boundingBox, motionX);
    }
    boundingBox = boundingBox.offset(motionX, 0.0D, 0.0D);
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionZ = collisionBox.calculateZOffset(boundingBox, motionZ);
    }
    return new CollisionResult(motionX, motionY, motionZ, onGround, startMotionY != motionY);
  }

  public static Vector resolvePushVector(Player player, double positionX, double positionY, double positionZ) {
    WrappedBlockPosition blockPosition = new WrappedBlockPosition(positionX, positionY, positionZ);
    double d0 = positionX - blockPosition.xCoord;
    double d1 = positionZ - blockPosition.zCoord;
    Vector vector = new Vector();
    int i = -1;
    double d2 = 9999.0D;
    if (isOpenBlockSpace(player, blockPosition.west()) && d0 < d2) {
      d2 = d0;
      i = 0;
    }
    if (isOpenBlockSpace(player, blockPosition.east()) && 1.0D - d0 < d2) {
      d2 = 1.0D - d0;
      i = 1;
    }
    if (isOpenBlockSpace(player, blockPosition.north()) && d1 < d2) {
      d2 = d1;
      i = 4;
    }
    if (isOpenBlockSpace(player, blockPosition.south()) && 1.0D - d1 < d2) {
      i = 5;
    }
    float f = 0.1F;
    if (i == 0) {
      vector.setX(-f);
    }
    if (i == 1) {
      vector.setX(f);
    }
    if (i == 4) {
      vector.setZ(-f);
    }
    if (i == 5) {
      vector.setZ(f);
    }
    return vector;
  }

  public static boolean checkBoundingBoxIntersection(User user, WrappedAxisAlignedBB boundingBox) {
    Player player = user.player();
    World world = player.getWorld();
    List<WrappedAxisAlignedBB> collisionBoxes = CollisionFactory.getCollisionBoxes(user.player(), boundingBox);
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      double positionX = (collisionBox.minX + collisionBox.maxX) / 2.0;
      double positionY = (collisionBox.minY + collisionBox.maxY) / 2.0;
      double positionZ = (collisionBox.minZ + collisionBox.maxZ) / 2.0;
      Block block = BlockAccessor.blockAccess(world, positionX, positionY, positionZ);
      Material type = block.getType();
      if (!CollisionFactory.blockIntersectionExclusionNames().contains(type.name())) {
        return true;
      }
    }
    return false;
  }

  public static boolean isOpenBlockSpace(Player player, WrappedBlockPosition pos) {
    return hasEmptyCollisionBox(player, pos) && hasEmptyCollisionBox(player, pos.up());
  }

  private static boolean hasEmptyCollisionBox(Player player, WrappedBlockPosition blockPosition) {
    return CollisionFactory.getCollisionBoxes(player, CollisionHelper.entityBoundingBoxOf(blockPosition)).isEmpty();
  }

  public static class CollisionResult {
    private final double motionX, motionY, motionZ;
    private final boolean onGround, collidedVertically;

    public CollisionResult(double motionX, double motionY, double motionZ, boolean onGround, boolean collidedVertically) {
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.onGround = onGround;
      this.collidedVertically = collidedVertically;
    }

    public double motionX() {
      return motionX;
    }

    public double motionY() {
      return motionY;
    }

    public double motionZ() {
      return motionZ;
    }

    public boolean onGround() {
      return onGround;
    }

    public boolean collidedVertically() {
      return collidedVertically;
    }
  }
}