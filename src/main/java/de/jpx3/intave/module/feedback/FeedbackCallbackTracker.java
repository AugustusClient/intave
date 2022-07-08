package de.jpx3.intave.module.feedback;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2022
 */

public interface FeedbackCallbackTracker extends FeedbackTracker {
  @Override
  default void sent(FeedbackRequest<?> request) {
  }

  @Override
  default void received(FeedbackRequest<?> request) {
    uponReceive();
  }

  void uponReceive();

  @Override
  default void failed() {}
}
