package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.block.state.BlockState;
import de.jpx3.intave.block.state.BlockStateAccess;
import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.klass.rewrite.PatchyTranslateParameters;
import de.jpx3.intave.shade.MovingObjectPosition;
import de.jpx3.intave.shade.NativeVector;
import de.jpx3.intave.user.UserRepository;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

@PatchyAutoTranslation
public final class v8Raytracer implements Raytracer {
  @Override
  @PatchyAutoTranslation
  public MovingObjectPosition raytrace(World world, Player player, NativeVector eyeVector, NativeVector targetVector) {
    WorldServer handle = ((CraftWorld) world).getHandle();
    Vec3D nativeEyeVector = (Vec3D) eyeVector.convertToNativeVec3();
    Vec3D nativeTargetVector = (Vec3D) targetVector.convertToNativeVec3();
    net.minecraft.server.v1_8_R3.MovingObjectPosition movingObjectPosition = performRaytrace(player, handle, nativeEyeVector, nativeTargetVector);
    return MovingObjectPosition.fromNativeMovingObjectPosition(movingObjectPosition);
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private net.minecraft.server.v1_8_R3.MovingObjectPosition performRaytrace(
    Player player, WorldServer world,
    Vec3D nativeLookVector, Vec3D nativePosition
  ) {
//    System.out.println("Raytrace " + nativeLookVector + " " + nativePosition);

    NativeVector lookVector = NativeVector.fromNative(nativeLookVector);
    NativeVector targetVector = NativeVector.fromNative(nativePosition);

    if (includesInvalidCoordinate(lookVector) || includesInvalidCoordinate(targetVector)) {
      return null;
    }

    net.minecraft.server.v1_8_R3.MovingObjectPosition movingobjectposition;
    int targetX = MathHelper.floor(targetVector.xCoord);
    int targetY = MathHelper.floor(targetVector.yCoord);
    int targetZ = MathHelper.floor(targetVector.zCoord);
    int lookX = MathHelper.floor(lookVector.xCoord);
    int lookY = MathHelper.floor(lookVector.yCoord);
    int lookZ = MathHelper.floor(lookVector.zCoord);

    BlockPosition blockposition = new BlockPosition(lookX, lookY, lookZ);
    IBlockData iblockdata = typeOf(player, world, blockposition);//world.getType(blockposition);
    Block block = iblockdata.getBlock();

    if (block.a(iblockdata, false)) {
      movingobjectposition = (net.minecraft.server.v1_8_R3.MovingObjectPosition)
        movingObjectPosition(world, block, blockposition, (Vec3D) lookVector.convertToNativeVec3(), (Vec3D) targetVector.convertToNativeVec3());
      if (movingobjectposition != null) {
        return movingobjectposition;
      }
    }

    int jumps = 50;
    while (jumps-- >= 0) {
      EnumDirection enumdirection;
      if (includesInvalidCoordinate(lookVector)) {
        return null;
      }
      if (lookX == targetX && lookY == targetY && lookZ == targetZ) {
        return null;
      }
      boolean arrivedAtX = true;
      boolean arrivedAtY = true;
      boolean arrivedAtZ = true;
      double lookXStep = 999.0;
      double lookYStep = 999.0;
      double lookZStep = 999.0;
      if (targetX > lookX) {
        lookXStep = (double) lookX + 1.0;
      } else if (targetX < lookX) {
        lookXStep = (double) lookX + 0.0;
      } else {
        arrivedAtX = false;
      }
      if (targetY > lookY) {
        lookYStep = (double) lookY + 1.0;
      } else if (targetY < lookY) {
        lookYStep = (double) lookY + 0.0;
      } else {
        arrivedAtY = false;
      }
      if (targetZ > lookZ) {
        lookZStep = (double) lookZ + 1.0;
      } else if (targetZ < lookZ) {
        lookZStep = (double) lookZ + 0.0;
      } else {
        arrivedAtZ = false;
      }
      double d3 = 999.0;
      double d4 = 999.0;
      double d5 = 999.0;
      double d6 = targetVector.xCoord - lookVector.xCoord;
      double d7 = targetVector.yCoord - lookVector.yCoord;
      double d8 = targetVector.zCoord - lookVector.zCoord;
      if (arrivedAtX) {
        d3 = (lookXStep - lookVector.xCoord) / d6;
      }
      if (arrivedAtY) {
        d4 = (lookYStep - lookVector.yCoord) / d7;
      }
      if (arrivedAtZ) {
        d5 = (lookZStep - lookVector.zCoord) / d8;
      }
      if (d3 == -0.0) {
        d3 = -0.0001;
      }
      if (d4 == -0.0) {
        d4 = -0.0001;
      }
      if (d5 == -0.0) {
        d5 = -0.0001;
      }
      if (d3 < d4 && d3 < d5) {
        enumdirection = targetX > lookX ? EnumDirection.WEST : EnumDirection.EAST;
        lookVector = new NativeVector(lookXStep, lookVector.yCoord + d7 * d3, lookVector.zCoord + d8 * d3);
      } else if (d4 < d5) {
        enumdirection = targetY > lookY ? EnumDirection.DOWN : EnumDirection.UP;
        lookVector = new NativeVector(lookVector.xCoord + d6 * d4, lookYStep, lookVector.zCoord + d8 * d4);
      } else {
        enumdirection = targetZ > lookZ ? EnumDirection.NORTH : EnumDirection.SOUTH;
        lookVector = new NativeVector(lookVector.xCoord + d6 * d5, lookVector.yCoord + d7 * d5, lookZStep);
      }
      lookX = MathHelper.floor(lookVector.xCoord) - (enumdirection == EnumDirection.EAST ? 1 : 0);
      lookY = MathHelper.floor(lookVector.yCoord) - (enumdirection == EnumDirection.UP ? 1 : 0);
      lookZ = MathHelper.floor(lookVector.zCoord) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);
      blockposition = new BlockPosition(lookX, lookY, lookZ);
      IBlockData iblockdata1 = typeOf(player, world, blockposition);
      Block block1 = iblockdata1.getBlock();

      // block1.a refers to isSolid
      boolean solid = block1.a(iblockdata1, false);
      if (solid) {
        net.minecraft.server.v1_8_R3.MovingObjectPosition finalObjectMovingPosition = (net.minecraft.server.v1_8_R3.MovingObjectPosition)
          movingObjectPosition(
            world,
            block1,
            blockposition,
            (Vec3D) lookVector.convertToNativeVec3(),
            (Vec3D) targetVector.convertToNativeVec3()
          );
        if (finalObjectMovingPosition != null) {
          return finalObjectMovingPosition;
        }
      }
    }
    return null;
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private Object movingObjectPosition(WorldServer world, Block block, BlockPosition blockPosition, Vec3D lookVector, Vec3D targetVector) {
    try {
      // inner block raytrace
      return block.a(world, blockPosition, lookVector, targetVector);
    } catch (Exception | Error exception) {
      return Blocks.STONE.a(world, blockPosition, lookVector, targetVector);
    }
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private IBlockData typeOf(Player player, WorldServer world, BlockPosition blockPosition) {
    BlockStateAccess blockStateAccess = UserRepository.userOf(player).blockStates();
    BlockState state = blockStateAccess.overrideOf(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    if (state != null) {
      return Block.getById(state.type().getId()).fromLegacyData(state.variantIndex());
    } else {
      return world.getType(blockPosition);
    }
  }

  private boolean includesInvalidCoordinate(NativeVector nativeVector) {
    return Double.isNaN(nativeVector.xCoord) || Double.isNaN(nativeVector.yCoord) || Double.isNaN(nativeVector.zCoord);
  }
}
