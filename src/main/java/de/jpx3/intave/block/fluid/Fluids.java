package de.jpx3.intave.block.fluid;

import de.jpx3.intave.IntaveLogger;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.block.state.BlockStateCache;
import de.jpx3.intave.block.state.ExtendedBlockStateCache;
import de.jpx3.intave.block.variant.BlockVariantRegister;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.share.BlockPosition;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.jpx3.intave.adapter.MinecraftVersions.*;

public class Fluids {
  private static final Map<Material, Map<Integer, Fluid>> liquidData = new HashMap<>();
  private static FluidResolver resolver;
  private static FluidFlow waterflow;

  public static void setup() {
    String className;
    if (VER1_18_2.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.next.v18b2LiquidResolver";
    } else if (VER1_16_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.next.v16LiquidResolver";
    } else if (VER1_14_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.next.v14LiquidResolver";
    } else if (VER1_13_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.next.v13LiquidResolver";
    } else {
      className = "de.jpx3.intave.block.fluid.next.v12LiquidResolver";
    }
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), className);
    try {
      resolver = (FluidResolver) Class.forName(className).newInstance();
    } catch (Exception exception) {
      throw new IntaveInternalException(exception);
    }

    waterflow = new WaterFlow();

    int variantCount = 0;
    int liquidCount = 0;
    for (Material value : Material.values()) {
      if (value.isBlock()) {
        boolean anyLiquid = false;
        Map<Integer, Fluid> variants = new HashMap<>();
        for (int variantIndex : BlockVariantRegister.variantIdsOf(value)) {
          Fluid currentFluid = resolver.liquidFrom(value, variantIndex);
          if (!currentFluid.isDry()) {
//            BlockVariant properties = BlockVariantRegister.uncachedVariantOf(value, variantIndex);
//            String propertyString = "{"+properties.propertyNames().stream().map(s -> s + ": " + properties.propertyOf(s)).collect(Collectors.joining(", ")) +"}";
//            System.out.println("Found liquid " + currentLiquid + " at " + value + ":" + propertyString);
            anyLiquid = true;
            liquidCount++;
          }
          variants.put(variantIndex, currentFluid);
          variantCount++;
        }
        if (anyLiquid) {
          liquidData.put(value, variants);
        }
      }
    }
    IntaveLogger.logger().info("Indexed " + liquidCount + " fluids from " + variantCount + " block variants");
  }

  public static FluidFlow waterflow() {
    return waterflow;
  }

  public static boolean canContainFluid(Material material) {
    return liquidData.containsKey(material);
  }

  public static boolean isFluid(Material material, int variantIndex) {
    Map<Integer, Fluid> liquidMappings = liquidData.get(material);
    return liquidMappings != null && liquidMappings.containsKey(variantIndex)
      && !liquidMappings.get(variantIndex).isDry();
  }

  public static @NotNull Fluid fluidStateOf(Material material, int variant) {
    Map<Integer, Fluid> map = liquidData.get(material);
    if (map == null) {
      return Dry.of();
    }
    Fluid fluid = map.get(variant);
    if (fluid == null) {
      return Dry.of();
    }
    return fluid;
  }

  public static @NotNull Fluid fluidAt(User user, Position position) {
    return fluidAt(user, position.getBlockX(), position.getBlockY(), position.getBlockZ());
  }

  public static @NotNull Fluid fluidAt(User user, Location location) {
    return fluidAt(user, location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public static @NotNull Fluid fluidAt(User user, BlockPosition position) {
    return fluidAt(user, position.getX(), position.getY(), position.getZ());
  }

  public static @NotNull Fluid fluidAt(User user, double x, double y, double z) {
    return fluidAt(user, floor(x), floor(y), floor(z));
  }

  public static @NotNull Fluid fluidAt(User user, int x, int y, int z) {
    BlockStateCache states = user.blockStates();
    Material type = states.typeAt(x, y, z);
    Map<Integer, Fluid> stateMap = liquidData.get(type);
    if (stateMap == null) {
      return Dry.of();
    }
    int variant = states.variantIndexAt(x, y, z);
    Fluid fluid = stateMap.get(variant);
    if (fluid == null) {
      return Dry.of();
    }
    return fluid;
  }

  public static boolean fluidPresentAt(User user, Position position) {
    return fluidPresentAt(user, position.getBlockX(), position.getBlockY(), position.getBlockZ());
  }

  public static boolean fluidPresentAt(User user, Location location) {
    return fluidPresentAt(user, location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public static boolean fluidPresentAt(User user, BlockPosition position) {
    return fluidPresentAt(user, position.getX(), position.getY(), position.getZ());
  }

  public static boolean fluidPresentAt(User user, double x, double y, double z) {
    return fluidPresentAt(user, floor(x), floor(y), floor(z));
  }

  public static boolean fluidPresentAt(User user, int x, int y, int z) {
    ExtendedBlockStateCache states = user.blockStates();
    Material type = states.typeAt(x, y, z);
    Map<Integer, Fluid> stateMap = liquidData.get(type);
    if (stateMap == null) {
      return false;
    }
    int variant = states.variantIndexAt(x, y, z);
    Fluid fluid = stateMap.get(variant);
    if (fluid == null) {
      return false;
    }
    return !fluid.isDry();
  }

  private static int floor(double value) {
    int i = (int) value;
    return value < (double) i ? i - 1 : i;
  }
}
