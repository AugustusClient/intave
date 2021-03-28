package de.jpx3.intave.access;

import de.jpx3.intave.tools.annotate.Relocate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Relocate
public abstract class IntaveEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  public IntaveEvent() {
    super(true);
  }

  public abstract void referenceInvalidate();

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
