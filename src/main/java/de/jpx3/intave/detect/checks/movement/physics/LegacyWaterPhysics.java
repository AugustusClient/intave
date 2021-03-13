package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.tools.client.ClientBlockHelper;
import de.jpx3.intave.tools.wrapper.*;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.world.BlockAccessor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public final class LegacyWaterPhysics {
  public static boolean handleMaterialAcceleration(User user, WrappedAxisAlignedBB boundingBox) {
    Player player = user.player();
    World world = player.getWorld();
    UserMetaMovementData movementData = user.meta().movementData();
    int minX = WrappedMathHelper.floor(boundingBox.minX);
    int minY = WrappedMathHelper.floor(boundingBox.minY);
    int minZ = WrappedMathHelper.floor(boundingBox.minZ);
    int maxX = WrappedMathHelper.floor(boundingBox.maxX + 1.0D);
    int maxY = WrappedMathHelper.floor(boundingBox.maxY + 1.0D);
    int maxZ = WrappedMathHelper.floor(boundingBox.maxZ + 1.0D);
    boolean inWater = false;
    WrappedVector flowVector = new WrappedVector(0, 0, 0);
    for (int x = minX; x < maxX; ++x) {
      for (int y = minY; y < maxY; ++y) {
        for (int z = minZ; z < maxZ; ++z) {
          Block block = BlockAccessor.blockAccess(world, x, y, z);
          Material clientSideBlock = BlockAccessor.cacheAppliedTypeAccess(user, world, x, y, z);
          boolean waterServerSide = ClientBlockHelper.isWater(block.getType());
          boolean waterClientSide = ClientBlockHelper.isWater(clientSideBlock);
          if (waterServerSide) {
            int level = resolveLiquidLevel(block);
            double d0 = (float) (y + 1) - resolveLiquidHeightPercentage(level);
            if ((double) maxY >= d0) {
              inWater = true;
              flowVector = modifyAcceleration(user, new WrappedBlockPosition(x, y, z), flowVector);
            }
          } else if (waterClientSide) {
            inWater = true;
          }
        }
      }
    }
    if (flowVector != null && flowVector.lengthVector() > 0.0D) {
      flowVector = flowVector.normalize();
      double d1 = 0.014D;
      movementData.physicsMotionX += flowVector.xCoord * d1;
      movementData.physicsMotionY += flowVector.yCoord * d1;
      movementData.physicsMotionZ += flowVector.zCoord * d1;
      movementData.pastPushedByWaterFlow = 0;
    }
    return inWater;
  }

  public static WrappedVector modifyAcceleration(User user, WrappedBlockPosition pos, WrappedVector motion) {
    return motion.add(flowVector(user.player().getWorld(), pos));
  }

  private static WrappedVector flowVector(World worldIn, WrappedBlockPosition pos) {
    WrappedVector vec3 = new WrappedVector(0.0D, 0.0D, 0.0D);
    int i = resolveEffectiveFlowDecay(worldIn, pos);
    for (WrappedEnumDirection enumDirection : WrappedEnumDirection.Plane.HORIZONTAL) {
      WrappedBlockPosition position = pos.offset(enumDirection);
      int j = resolveEffectiveFlowDecay(worldIn, position);
      if (j < 0) {
        if (!blocksMovement(worldIn, pos)) {
          j = resolveEffectiveFlowDecay(worldIn, position.down());
          if (j >= 0) {
            int k = j - (i - 8);
            vec3 = vec3.addVector((position.xCoord - pos.xCoord) * k, (position.yCoord - pos.yCoord) * k, (position.zCoord - pos.zCoord) * k);
          }
        }
      } else {
        int l = j - i;
        vec3 = vec3.addVector((position.xCoord - pos.xCoord) * l, (position.yCoord - pos.yCoord) * l, (position.zCoord - pos.zCoord) * l);
      }
    }
    if (resolveLevel(worldIn, pos) >= 8) {
      for (WrappedEnumDirection enumfacing1 : WrappedEnumDirection.Plane.HORIZONTAL) {
        WrappedBlockPosition blockpos1 = pos.offset(enumfacing1);
        if (isBlockSolid(worldIn, blockpos1, enumfacing1) || isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
          vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
          break;
        }
      }
    }
    return vec3.normalize();
  }

  private static boolean blocksMovement(World world, WrappedBlockPosition position) {
    Material type = blockAt(world, position).getType();
    return ClientBlockHelper.blocksMovement(type);
  }

  private static boolean isBlockSolid(World worldIn, WrappedBlockPosition pos, WrappedEnumDirection side) {
    Block block = blockAt(worldIn, pos);
    Material type = block.getType();
    return !ClientBlockHelper.isLiquid(type) && (side == WrappedEnumDirection.UP || (type != Material.ICE && ClientBlockHelper.blockSolid(blockAt(worldIn, pos).getType())));
  }

  private static Block blockAt(World world, WrappedBlockPosition position) {
    return BlockAccessor.blockAccess(world, position.xCoord, position.yCoord, position.zCoord);
  }

  private static int resolveLevel(World world, WrappedBlockPosition pos) {
    Block block = blockAt(world, pos);
    if (!ClientBlockHelper.isWater(block.getType())) {
      return -1;
    }
    return resolveLiquidLevel(block);
  }

  public static float resolveLiquidHeightPercentage(int meta) {
    if (meta >= 8) {
      meta = 0;
    }
    return (float) (meta + 1) / 9.0F;
  }

  public static int resolveLiquidLevel(Block block) {
    BlockState state = block.getState();
    //noinspection deprecation
    return state.getData().getData();
  }

  public static int resolveEffectiveFlowDecay(World world, WrappedBlockPosition pos) {
    int i = resolveLevel(world, pos);
    return i >= 8 ? 0 : i;
  }
}