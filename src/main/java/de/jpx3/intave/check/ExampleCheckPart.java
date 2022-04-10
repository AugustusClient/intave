package de.jpx3.intave.check;

import de.jpx3.intave.check.combat.ClickPatterns;

import static de.jpx3.intave.check.ExampleCheckPart.ExampleMeta;

public final class ExampleCheckPart extends ExampleBlueprint<ExampleMeta> {
  public ExampleCheckPart(ClickPatterns parentCheck) {
    super(parentCheck, ExampleMeta.class, 4);
  }

  public void execute() {
    ExampleMeta meta = metaOf(userOf(null));
    meta.specialString = "";
  }

  public static class ExampleMeta extends ExampleBlueprintMeta {
    public String test;
  }
}
