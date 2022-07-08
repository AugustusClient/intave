package de.jpx3.intave.module.feedback;

import de.jpx3.intave.annotate.Nullable;

public interface FeedbackTracker {
  void sent(FeedbackRequest<?> request);
  void received(FeedbackRequest<?> request);
  void failed();

  @Nullable
  static FeedbackTracker merge(FeedbackTracker... trackers) {
    if (trackers.length == 0) {
      return null;
    } else if (trackers.length == 1) {
      return trackers[0];
    } else {
      return new FeedbackTracker() {
        @Override
        public void sent(FeedbackRequest<?> request) {
          for (FeedbackTracker tracker : trackers) {
            tracker.sent(request);
          }
        }

        @Override
        public void received(FeedbackRequest<?> request) {
          for (FeedbackTracker tracker : trackers) {
            tracker.received(request);
          }
        }

        @Override
        public void failed() {
          for (FeedbackTracker tracker : trackers) {
            tracker.failed();
          }
        }
      };
    }
  }
}
