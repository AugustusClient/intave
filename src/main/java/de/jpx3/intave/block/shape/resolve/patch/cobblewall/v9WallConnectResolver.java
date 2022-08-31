package de.jpx3.intave.block.shape.resolve.patch.cobblewall;

import de.jpx3.intave.block.access.VolatileBlockAccess;
import de.jpx3.intave.share.BlockPosition;
import de.jpx3.intave.share.Direction;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.Gate;

public class v9WallConnectResolver implements WallConnectResolver {
  @Override
  public boolean canConnectTo(World world, BlockPosition position, Direction direction) {
    Material type = VolatileBlockAccess.blockAccess(world, position).getType();
    return type != Material.BARRIER
        && (type == Material.COBBLE_WALL || type.getData() == Gate.class || type.isOccluding());
  }
}
