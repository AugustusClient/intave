package de.jpx3.intave.world;

import de.jpx3.intave.adapter.MinecraftVersions;

@SuppressWarnings("PointlessArithmeticExpression")
public final class WorldHeight {
  private static final boolean MINECRAFT_18 = MinecraftVersions.VER1_18_0.atOrAbove();
  public static final int UPPER_WORLD_LIMIT = MINECRAFT_18 ? 256 + 64 : 256;
  public static final int LOWER_WORLD_LIMIT = MINECRAFT_18 ?   0 - 64 : 0;
}
