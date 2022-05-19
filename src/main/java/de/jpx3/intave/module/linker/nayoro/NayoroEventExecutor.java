package de.jpx3.intave.module.linker.nayoro;

import de.jpx3.intave.module.nayoro.PlayerContainer;
import de.jpx3.intave.module.nayoro.event.Event;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2022
 */

public interface NayoroEventExecutor {
  void execute(NayoroEventSubscriber subscriber, PlayerContainer player, Event event);
}
