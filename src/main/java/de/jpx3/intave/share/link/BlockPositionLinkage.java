package de.jpx3.intave.share.link;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.share.BlockPosition;

public final class BlockPositionLinkage {
  static ClassConverter<BlockPosition> resolveBlockPositionConverter() {
    String boundingBoxResolverClass = "de.jpx3.intave.share.link.BlockPositionLinkage$BlockPositionResolver";
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), boundingBoxResolverClass);
    return new BlockPositionResolver();
  }

  @PatchyAutoTranslation
  public static final class BlockPositionResolver implements ClassConverter<BlockPosition> {
    @PatchyAutoTranslation
    @Override
    public BlockPosition convert(Object obj) {
      net.minecraft.server.v1_8_R3.BlockPosition blockPosition = (net.minecraft.server.v1_8_R3.BlockPosition) obj;
      return new BlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }
  }
}