package de.jpx3.intave.block.fluid;

final class Dry implements Fluid {
  private static final Dry INSTANCE = new Dry();

  @Override
  public boolean isOfWater() {
    return false;
  }

  @Override
  public boolean isOfLava() {
    return false;
  }

  @Override
  public boolean isDry() {
    return true;
  }

  @Override
  public float height() {
    return 0;
  }

  @Override
  public int level() {
    return 0;
  }

  @Override
  public boolean falling() {
    return false;
  }

  @Override
  public boolean isSource() {
    return false;
  }

  @Override
  public String toString() {
    return "Dry{}";
  }

  static Dry of() {
    return INSTANCE;
  }
}
