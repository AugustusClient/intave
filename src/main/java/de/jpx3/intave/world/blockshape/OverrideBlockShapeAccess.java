package de.jpx3.intave.world.blockshape;

import org.bukkit.Location;

import java.util.Map;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2021
 */

public interface OverrideBlockShapeAccess extends BlockShapeAccess {
 boolean currentlyInOverride(int posX, int posY, int posZ);

 BlockShape overrideOf(int posX, int posY, int posZ);

 void invalidateOverride(int posX, int posY, int posZ);

 Map<Location, BlockShape> locatedReplacements();

 Map<Long, BlockShape> indexedReplacements();
}
