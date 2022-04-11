package de.jpx3.intave.check.combat.heuristics.detect.clickpatterns;

import de.jpx3.intave.check.combat.Heuristics;
import de.jpx3.intave.check.combat.heuristics.Anomaly;
import de.jpx3.intave.check.combat.heuristics.Confidence;
import de.jpx3.intave.user.User;

import java.util.List;

import static de.jpx3.intave.check.combat.heuristics.detect.clickpatterns.SwingDeviationHeuristics.SwingDeviationBlueprintMeta;

public final class SwingDeviationHeuristics extends SwingBlueprint<SwingDeviationBlueprintMeta> {
  public SwingDeviationHeuristics(Heuristics parentCheck) {
    super(parentCheck, SwingDeviationBlueprintMeta.class, 60, true, false);
  }

  @Override
  public void check(User user, List<Integer> delays) {
    SwingDeviationBlueprintMeta exampleMeta = metaOf(userOf(user.player()));
    double cps = clickPerSecond(delays);
    double deviation = standardDeviation(delays);
    if (cps > 15 && deviation ) {
      String description = String.format("clicking too fast without double clicks %.2f", cps);
      Anomaly anomaly = Anomaly.anomalyOf("300", Confidence.NONE, Anomaly.Type.AUTOCLICKER, description);
      parentCheck().saveAnomaly(user.player(), anomaly);
    }
  }

  public static class SwingDeviationBlueprintMeta extends SwingBlueprintMeta {

    // Nothing yet!
  }
}