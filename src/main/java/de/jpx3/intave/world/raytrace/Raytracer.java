package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.MovingObjectPosition;
import de.jpx3.intave.shade.NativeVector;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface Raytracer {
  default MovingObjectPosition modifiedRaytrace(World world, Player player, NativeVector eyeVector, NativeVector targetVector, Function<BoundingBox, BoundingBox> blockModifier) {
    return raytrace(world, player, eyeVector, targetVector);
  }

  MovingObjectPosition raytrace(World world, Player player, NativeVector eyeVector, NativeVector targetVector);
}
