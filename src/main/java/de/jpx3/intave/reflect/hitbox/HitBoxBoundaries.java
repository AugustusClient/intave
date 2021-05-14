package de.jpx3.intave.reflect.hitbox;

public final class HitBoxBoundaries {
  private final float width;
  private final float length;

  private HitBoxBoundaries(float width, float length) {
    this.width = width;
    this.length = length;
  }

  public static HitBoxBoundaries of(float width, float length) {
    return new HitBoxBoundaries(width, length);
  }

  public static HitBoxBoundaries zero() {
    return new HitBoxBoundaries(0, 0);
  }

  public static HitBoxBoundaries player() {
    return new HitBoxBoundaries(0.6f, 1.8f);
  }

  public float width() {
    return width;
  }

  public float length() {
    return length;
  }

  @Override
  public String toString() {
    return "HitBoxBoundaries{" +
      "width=" + width +
      ", length=" + length +
      '}';
  }
}