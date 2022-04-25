package de.jpx3.intave.check.combat.heuristics.detect.experimental;

import de.jpx3.intave.user.meta.CheckCustomMetadata;

import java.util.ArrayList;
import java.util.List;

public class RotationPrevisionBlueprintMeta extends CheckCustomMetadata {
  protected final List<RotationData> rotationValues = new ArrayList<>();
  protected int lastAttack; // In client ticks

  public static class RotationData {
    protected final float yawDelta;
    protected final float expectedYawDelta;
    protected final float pitchDelta;
    protected final float expectedPitchDelta;

    public RotationData(float yawDelta, float expectedYawDelta, float pitchDelta, float expectedPitchDelta) {
      this.yawDelta = yawDelta;
      this.expectedYawDelta = expectedYawDelta;
      this.pitchDelta = pitchDelta;
      this.expectedPitchDelta = expectedPitchDelta;
    }
  }
}
