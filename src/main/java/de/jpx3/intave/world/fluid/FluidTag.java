package de.jpx3.intave.world.fluid;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.reflect.ReflectiveAccess;
import de.jpx3.intave.tools.annotate.KeepEnumInternalNames;

@KeepEnumInternalNames
public enum FluidTag {
  WATER(true),
  LAVA(true),
  EMPTY(false);

  private final boolean real;

  FluidTag(boolean real) {
    this.real = real;
  }

  private Object nativeTag;

  public Object nativeTag() {
    if (!this.real) {
      throw new IntaveInternalException("Cannot resolve actual fluid tag");
    }
    if (this.nativeTag == null) {
      this.nativeTag = resolveNativeTag();
    }
    return this.nativeTag;
  }

  private Object resolveNativeTag() {
    try {
      return ReflectiveAccess.lookupServerClass("TagsFluid").getField(name()).get(null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IntaveInternalException("Cannot access fluid tag", e);
    }
  }
}