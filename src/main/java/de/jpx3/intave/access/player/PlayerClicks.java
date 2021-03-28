package de.jpx3.intave.access.player;

import de.jpx3.intave.tools.annotate.Relocate;

import java.util.function.Consumer;

@Relocate
public interface PlayerClicks {
  int clicksLastSecond();
  void subscribeToSecond(Consumer<Integer> clicks);
}