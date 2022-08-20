package de.jpx3.intave.module.feedback;

import de.jpx3.intave.IntaveControl;
import de.jpx3.intave.IntaveLogger;
import org.bukkit.entity.Player;

public final class FeedbackRequest<T> {
  private final FeedbackCallback<T> callback;
  private final FeedbackTracker tracker;
  private final T obj;
  private final short key;
  private final long num;
  private final long time;

  FeedbackRequest(FeedbackCallback<T> callback, FeedbackTracker tracker, T obj, short key, long num) {
    this.callback = callback;
    this.tracker = tracker;
    this.obj = obj;
    this.key = key;
    this.num = num;
    this.time = System.currentTimeMillis();
  }

  void sent() {
    if (tracker != null) {
      tracker.sent(this);
    }
  }

  void acknowledge(Player player) {
    try {
      callback.success(player, obj);
      if (tracker != null) {
        tracker.received(this);
      }
    } catch (Exception e) {
      if (IntaveControl.DISABLE_LICENSE_CHECK) {
        IntaveLogger.logger().error("Error while acknowledging " + callback + " for " + player);
        e.printStackTrace();
      }
    }
  }

  T target() {
    return obj;
  }

  FeedbackCallback<T> callback() {
    return callback;
  }

  short key() {
    return key;
  }

  long num() {
    return num;
  }

  public long requested() {
    return time;
  }

  public long passedTime() {
    return System.currentTimeMillis() - this.time;
  }
}