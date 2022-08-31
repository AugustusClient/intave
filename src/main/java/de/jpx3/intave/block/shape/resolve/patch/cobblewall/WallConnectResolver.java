package de.jpx3.intave.block.shape.resolve.patch.cobblewall;

import de.jpx3.intave.share.BlockPosition;
import de.jpx3.intave.share.Direction;
import org.bukkit.World;

public interface WallConnectResolver {
  boolean canConnectTo(World world, BlockPosition position, Direction direction);
}
