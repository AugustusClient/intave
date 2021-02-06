package de.jpx3.intave.detect.checks.movement.physics.water;

import de.jpx3.intave.tools.wrapper.*;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.world.BlockAccessor;
import de.jpx3.intave.world.BlockLiquidHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public final class WaterMovementLegacyResolver {

  public static boolean handleMaterialAcceleration(User user, WrappedAxisAlignedBB boundingBox) {
    Player player = user.player();
    UserMetaMovementData movementData = user.meta().movementData();
    int minX = WrappedMathHelper.floor(boundingBox.minX);
    int minY = WrappedMathHelper.floor(boundingBox.minY);
    int minZ = WrappedMathHelper.floor(boundingBox.minZ);
    int maxX = WrappedMathHelper.floor(boundingBox.maxX + 1.0D);
    int maxY = WrappedMathHelper.floor(boundingBox.maxY + 1.0D);
    int maxZ = WrappedMathHelper.floor(boundingBox.maxZ + 1.0D);
    boolean inWater = false;
    WrappedVector flowVector = null;

    for (int x = minX; x < maxX; ++x) {
      for (int y = minY; y < maxY; ++y) {
        for (int z = minZ; z < maxZ; ++z) {
          Block block = BlockAccessor.blockAccess(player.getWorld(), x, y, z);
          if (BlockLiquidHelper.isWater(block.getType())) {
            int level = resolveLiquidLevel(block);
            double d0 = (float) (y + 1) - resolveLiquidHeightPercentage(level);
            if ((double) maxY >= d0) {
              inWater = true;
              WrappedBlockPosition blockPosition = new WrappedBlockPosition(x, y, z);
              flowVector = resolveWaterFlowVector(player.getWorld(), blockPosition);
            }
          }
        }
      }
      if (flowVector != null && flowVector.lengthVector() > 0.0D) {
        flowVector = flowVector.normalize();
        double d1 = 0.014D;
        movementData.physicsLastMotionX += flowVector.xCoord * d1;
        movementData.physicsLastMotionY += flowVector.yCoord * d1;
        movementData.physicsLastMotionZ += flowVector.zCoord * d1;
        movementData.pastPushedByWaterFlow = 0;
      }
    }
    return inWater;
  }


//  public static boolean handleMaterialAcceleration(User user, WrappedAxisAlignedBB bb) {
//    Player player = user.player();
//    UserMetaMovementData movementData = user.meta().movementData();
//
//    World world = player.getWorld();
//
//    int i = WrappedMathHelper.floor(bb.minX);
//    int j = WrappedMathHelper.floor(bb.maxX + 1.0D);
//    int k = WrappedMathHelper.floor(bb.minY);
//    int l = WrappedMathHelper.floor(bb.maxY + 1.0D);
//    int i1 = WrappedMathHelper.floor(bb.minZ);
//    int j1 = WrappedMathHelper.floor(bb.maxZ + 1.0D);
//
//    boolean flag = false;
//    WrappedVector vec3 = new WrappedVector(0.0D, 0.0D, 0.0D);
//
//    for (int x = i; x < j; ++x) {
//      for (int y = k; y < l; ++y) {
//        for (int z = i1; z < j1; ++z) {
//          Block block = BlockAccessor.blockAccess(user.player().getWorld(), x, y, z);
//
//          if (BlockLiquidHelper.isWater(block.getType())) {
//            float liquidHeightPercent = resolveLiquidHeightPercentage(resolveLiquidLevel(block));
//            double d0 = (float) (y + 1) - ((int) liquidHeightPercent);
//            if ((double) l >= d0) {
//              flag = true;
//              WrappedBlockPosition blockPosition = new WrappedBlockPosition(x, y, z);
//              vec3 = resolveWaterFlowVector(world, blockPosition);
//            }
//          }
//        }
//      }
//    }
//
//    if (vec3.lengthVector() > 0.0D) {
//      vec3 = vec3.normalize();
//      double d1 = 0.014D;
//      movementData.physicsLastMotionX += vec3.xCoord * d1;
//      movementData.physicsLastMotionY += vec3.yCoord * d1;
//      movementData.physicsLastMotionZ += vec3.zCoord * d1;
//    }
//    return flag;
//  }

//  protected Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos) {
//    Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
//    int i = this.getEffectiveFlowDecay(worldIn, pos);
//
//    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
//      BlockPos blockpos = pos.offset(enumfacing);
//      int j = this.getEffectiveFlowDecay(worldIn, blockpos);
//
//      if (j < 0) {
//        if (!worldIn.getBlockState(blockpos).getBlock().getMaterial().blocksMovement()) {
//          j = this.getEffectiveFlowDecay(worldIn, blockpos.down());
//
//          if (j >= 0) {
//            int k = j - (i - 8);
//            vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * k, (blockpos.getY() - pos.getY()) * k, (blockpos.getZ() - pos.getZ()) * k);
//          }
//        }
//      } else {
//        int l = j - i;
//        vec3 = vec3.addVector((blockpos.getX() - pos.getX()) * l, (blockpos.getY() - pos.getY()) * l, (blockpos.getZ() - pos.getZ()) * l);
//      }
//    }
//
//    if (worldIn.getBlockState(pos).getValue(LEVEL) >= 8) {
//      for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
//        BlockPos blockpos1 = pos.offset(enumfacing1);
//
//        if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
//          vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
//          break;
//        }
//      }
//    }
//
//    return vec3.normalize();
//  }

  public static WrappedVector resolveWaterFlowVector(
    World world,
    WrappedBlockPosition pos
  ) {
    WrappedVector vec3 = new WrappedVector(0.0D, 0.0D, 0.0D);
    int flowDecay = resolveEffectiveFlowDecay(world, pos);

    for (WrappedEnumDirection enumDirection : WrappedEnumDirection.Plane.HORIZONTAL) {
      WrappedBlockPosition blockPosition = pos.offset(enumDirection);
      Location location = new Location(world, blockPosition.xCoord, blockPosition.yCoord, blockPosition.zCoord);
      int effectiveFlowDecay = resolveEffectiveFlowDecay(world, blockPosition);
      if (effectiveFlowDecay < 0) {
        if (!BlockLiquidHelper.isLiquid(BlockAccessor.blockAccess(location).getType())) {
//        if (!worldIn.getBlockState(blockPosition).getBlock().getMaterial().blocksMovement()) {
          effectiveFlowDecay = resolveEffectiveFlowDecay(world, blockPosition.down());
          if (effectiveFlowDecay >= 0) {
            int k = effectiveFlowDecay - (flowDecay - 8);
            vec3 = vec3.addVector(
              (blockPosition.xCoord - pos.xCoord) * k,
              (blockPosition.yCoord - pos.yCoord) * k,
              (blockPosition.zCoord - pos.zCoord) * k
            );
          }
        }
      } else {
        int l = effectiveFlowDecay - flowDecay;
        vec3 = vec3.addVector(
          (blockPosition.xCoord - pos.xCoord) * l,
          (blockPosition.yCoord - pos.yCoord) * l,
          (blockPosition.zCoord - pos.zCoord) * l
        );
      }
    }

    if (resolveLevel(world, pos) >= 8) {
      for (WrappedEnumDirection enumDirection : WrappedEnumDirection.Plane.HORIZONTAL) {
        WrappedBlockPosition blockPosition = pos.offset(enumDirection);
        Location location = new Location(world, blockPosition.xCoord, blockPosition.yCoord, blockPosition.zCoord);
        switch (enumDirection) {
          case NORTH: {
            location.add(0, 0, -1);
            break;
          }
          case EAST: {
            location.add(1, 0, 0);
            break;
          }
          case SOUTH: {
            location.add(0, 0, 1);
            break;
          }
          case WEST: {
            location.add(-1, 0, 0);
            break;
          }
        }
        if (BlockAccessor.blockAccess(location).getType().isSolid() || BlockAccessor.blockAccess(location.add(0.0, 1.0, 0.0)).getType().isSolid()) {
          vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
          break;
        }
      }
    }

    return vec3.normalize();
  }

  private static int resolveLevel(World world, WrappedBlockPosition pos) {
    Location location = new Location(world, pos.xCoord, pos.yCoord, pos.zCoord);
    Block blockAccess = BlockAccessor.blockAccess(location);
    if (!BlockLiquidHelper.isWater(blockAccess.getType())) {
      return -1;
    }
    return resolveLiquidLevel(blockAccess);
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