package de.jpx3.intave.block.shape;

import de.jpx3.intave.annotate.Nullable;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.Direction;
import de.jpx3.intave.shade.Position;

import java.util.List;

public interface BlockShape {
  double allowedOffset(Direction.Axis axis, BoundingBox entity, double offset);
  double min(Direction.Axis axis);
  double max(Direction.Axis axis);

  boolean intersectsWith(BoundingBox boundingBox);
  BlockShape contextualized(int posX, int posY, int posZ);
  BlockShape normalized(int posX, int posY, int posZ);

  @Nullable
  BlockRaytrace raytrace(Position origin, Position target);

  @Deprecated
  List<BoundingBox> boundingBoxes();
  boolean isEmpty();
}
