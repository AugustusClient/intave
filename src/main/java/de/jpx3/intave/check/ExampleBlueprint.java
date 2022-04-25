package de.jpx3.intave.check;

import de.jpx3.intave.check.combat.ClickPatterns;

public abstract class ExampleBlueprint<M extends ExampleBlueprintMeta>
  extends BlueprintLayout<ClickPatterns, ExampleBlueprintMeta, M> {
  private final int sampleSize;

  public ExampleBlueprint(ClickPatterns parentCheck, Class<M> blueprintMetaClass, int sampleSize) {
    super(parentCheck, blueprintMetaClass);
    this.sampleSize = sampleSize;
  }

}
