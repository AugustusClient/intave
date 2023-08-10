package de.jpx3.intave.check.combat.clickpatterns;

import de.jpx3.intave.check.combat.ClickPatterns;
import de.jpx3.intave.user.User;

public final class Bursts extends TickAlignedHistoryBlueprint<Bursts.BurstMeta> {
  public Bursts(ClickPatterns parentCheck) {
    super(parentCheck, BurstMeta.class);
  }

  @Override
  public void analyzeClicks(User user, BurstMeta meta) {
    int accumulativeConsecutiveClicks = 0;
    int consecutiveClicks = 0;
    for (TickAction tickAction : meta.tickActions) {
      if (tickAction == TickAction.CLICK || tickAction == TickAction.ATTACK) {
        consecutiveClicks++;
      } else {
        consecutiveClicks = 0;
      }
      if (consecutiveClicks > 4) {
        accumulativeConsecutiveClicks += consecutiveClicks;
      }
    }

    if (accumulativeConsecutiveClicks > 10) {
      flag(user, "exhibits repetitive bursts of clicks");
    }
  }

  public static class BurstMeta extends TickAlignedMeta {

  }
}

