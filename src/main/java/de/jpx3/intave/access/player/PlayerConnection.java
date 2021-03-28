package de.jpx3.intave.access.player;

import de.jpx3.intave.tools.annotate.Relocate;

import java.util.function.BiConsumer;

@Relocate
public interface PlayerConnection {
  int latency();
  int latencyJitter();
  void subscribe(BiConsumer<Integer, Integer> callback);
  long packetSentByClient();
  long packetSentToClient();
}