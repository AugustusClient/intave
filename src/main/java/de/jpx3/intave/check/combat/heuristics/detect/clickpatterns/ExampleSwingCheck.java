package de.jpx3.intave.check.combat.heuristics.detect.clickpatterns;

import de.jpx3.intave.check.combat.Heuristics;

import java.util.List;

import static de.jpx3.intave.check.combat.heuristics.detect.clickpatterns.ExampleSwingCheck.ExampleBlueprintMeta;

public final class ExampleSwingCheck extends SwingBlueprint<ExampleBlueprintMeta> {
  public ExampleSwingCheck(Heuristics parentCheck) {
    super(parentCheck, ExampleBlueprintMeta.class, 300);
  }

  @Override
  public void check(List<Integer> delays) {
    ExampleBlueprintMeta exampleMeta = metaOf(userOf(null));
  }

  public static class ExampleBlueprintMeta extends SwingBlueprintMeta {

    public String richy = "";
  }
}
