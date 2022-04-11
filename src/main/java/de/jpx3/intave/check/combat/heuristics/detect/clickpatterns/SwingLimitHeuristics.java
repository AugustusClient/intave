package de.jpx3.intave.check.combat.heuristics.detect.clickpatterns;

import de.jpx3.intave.check.combat.Heuristics;
import de.jpx3.intave.check.combat.heuristics.Anomaly;
import de.jpx3.intave.check.combat.heuristics.Confidence;
import de.jpx3.intave.user.User;

import java.util.List;

import static de.jpx3.intave.check.combat.heuristics.detect.clickpatterns.SwingLimitHeuristics.SwingLimitBlueprintMeta;

public final class SwingLimitHeuristics extends SwingBlueprint<SwingLimitBlueprintMeta> {
  public SwingLimitHeuristics(Heuristics parentCheck) {
    super(parentCheck, SwingLimitBlueprintMeta.class, 60, false, false);
  }

  @Override
  public void check(User user, List<Integer> delays) {
    SwingLimitBlueprintMeta exampleMeta = metaOf(userOf(user.player()));
    double cps = clickPerSecond(delays);
    if (cps > 15 && exampleMeta.doubleClicks == 0) {
      String description = String.format("clicking too fast without double clicks %.2f", cps);
      Anomaly anomaly = Anomaly.anomalyOf("300", Confidence.NONE, Anomaly.Type.AUTOCLICKER, description);
      parentCheck().saveAnomaly(user.player(), anomaly);
    }
  }

  public static class SwingLimitBlueprintMeta extends SwingBlueprintMeta {
    // Nothing yet!
  }
}