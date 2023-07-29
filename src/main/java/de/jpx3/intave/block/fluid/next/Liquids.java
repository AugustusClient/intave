package de.jpx3.intave.block.fluid.next;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.block.state.BlockStateCache;
import de.jpx3.intave.block.state.ExtendedBlockStateCache;
import de.jpx3.intave.block.variant.BlockVariantRegister;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.jpx3.intave.adapter.MinecraftVersions.*;

public class Liquids {
  private static LiquidResolver resolver;
  private final static Map<Material, Map<Integer, Liquid>> liquids = new HashMap<>();

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
      resolver = (LiquidResolver) Class.forName(className).newInstance();
    } catch (Exception exception) {
      throw new IntaveInternalException(exception);
    }
//    int variantCount = 0;
    for (Material value : Material.values()) {
      if (value.isBlock()) {
        Map<Integer, Liquid> variants = new HashMap<>();
        for (int variantIndex : BlockVariantRegister.variantIdsOf(value)) {
//          variantCount++;
          Liquid currentLiquid = resolver.liquidFrom(value, variantIndex);
//          if (!currentLiquid.isDry()) {
//            BlockVariant properties = BlockVariantRegister.uncachedVariantOf(value, variantIndex);
//            String propertyString = "{"+properties.propertyNames().stream().map(s -> s + ": " + properties.propertyOf(s)).collect(Collectors.joining(", ")) +"}";
//            System.out.println("Found liquid " + currentLiquid + " at " + value + ":" + propertyString);
//          }
          variants.put(variantIndex, currentLiquid);
        }
        liquids.put(value, variants);
      }
    }
//    System.out.println("Checked " + variantCount + " variants");
  }

  @Deprecated
  public static boolean isLiquid(Material material) {
    return liquids.containsKey(material);
  }

  @Deprecated
  public static @NotNull Liquid liquidStateOf(Material material, int variant) {
    Map<Integer, Liquid> map = liquids.get(material);
    if (map == null) {
      return Dry.of();
    }
    Liquid liquid = map.get(variant);
    if (liquid == null) {
      return Dry.of();
    }
    return liquid;
  }

  public static @NotNull Liquid liquidAt(User user, Position position) {
    return liquidAt(user, position.getBlockX(), position.getBlockY(), position.getBlockZ());
  }

  public static @NotNull Liquid liquidAt(User user, Location location) {
    return liquidAt(user, location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public static @NotNull Liquid liquidAt(User user, int x, int y, int z) {
    BlockStateCache states = user.blockStates();
    Material type = states.typeAt(x, y, z);
    Map<Integer, Liquid> stateMap = liquids.get(type);
    if (stateMap == null) {
      return Dry.of();
    }
    int variant = states.variantIndexAt(x, y, z);
    Liquid liquid = stateMap.get(variant);
    if (liquid == null) {
      return Dry.of();
    }
    return liquid;
  }

  public static boolean liquidPresentAt(User user, int x, int y, int z) {
    ExtendedBlockStateCache states = user.blockStates();
    Material type = states.typeAt(x, y, z);
    Map<Integer, Liquid> stateMap = liquids.get(type);
    if (stateMap == null) {
      return false;
    }
    int variant = states.variantIndexAt(x, y, z);
    Liquid liquid = stateMap.get(variant);
    if (liquid == null) {
      return false;
    }
    return !liquid.isDry();
  }
}
