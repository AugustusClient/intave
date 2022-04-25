package de.jpx3.intave.shade;

import org.bukkit.util.Vector;

import java.io.Serializable;

import static de.jpx3.intave.shade.ClientMathHelper.floor;

public final class Position extends Vector implements Serializable {
  public Position() {
  }

  public Position(double xCoordinate, double yCoordinate, double zCoordinate) {
    super(xCoordinate, yCoordinate, zCoordinate);
  }

  public boolean hasNaNCoordinate() {
    return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
  }

  public BlockPosition toBlockPosition() {
    return new BlockPosition(floor(x), floor(y), floor(z));
  }

  @Override
  public Position clone() {
    return (Position) super.clone();
  }

  public static Position empty() {
    return new Position();
  }
}
