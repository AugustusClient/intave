package de.jpx3.intave.access.server;

import de.jpx3.intave.tools.annotate.Relocate;

import java.util.function.Consumer;

@Relocate
public interface ServerHealthStatisticAccess {
  double tickAverageOver(TimeSpan timeSpan);
  void subscribeToTick(TimeSpan timeSpan, Consumer<Double> average);

  @Relocate
  enum TimeSpan {
    LAST_MINUTE,
    LAST_FIVE_MINUTES,
    LAST_TEN_MINUTES
  }
}
