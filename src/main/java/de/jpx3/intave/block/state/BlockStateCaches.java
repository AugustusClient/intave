package de.jpx3.intave.block.state;

import de.jpx3.intave.block.shape.ShapeResolverPipeline;
import de.jpx3.intave.block.shape.resolve.ShapeResolver;
import org.bukkit.entity.Player;

public final class BlockStateCaches {
  public static BlockStateExtendedCache forPlayer(Player player) {
    return forPlayerWithResolver(player, ShapeResolver.pipelineHead());
  }

  public static BlockStateExtendedCache forPlayerWithResolver(Player player, ShapeResolverPipeline resolver) {
    return new MultiChunkKeyBlockStateExtendedCache(player, resolver);
  }

  public static BlockStateExtendedCache empty() {
    return new EmptyBlockStateExtendedCache();
  }
}
