package de.jpx3.intave.analytics;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.jpx3.intave.diagnostic.timings.Timing;
import de.jpx3.intave.diagnostic.timings.Timings;

import java.util.List;
import java.util.Set;

import static de.jpx3.intave.analytics.DataCategory.USAGE;

public final class TimingsRecorder extends Recorder {
  @Override
  public String name() {
    return "timings";
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    List<Timing> timings = Timings.timingPool();
    JsonArray packetTimings = new JsonArray();
    JsonArray eventTimings = new JsonArray();
    JsonArray intaveTimings = new JsonArray();
    for (Timing timing : timings) {
      JsonObject timingJson = new JsonObject();
      timingJson.addProperty("name", timing.name());
      timingJson.addProperty("calls", timing.recordedCalls());
      timingJson.addProperty("total-ns", timing.totalDurationNanos());
      if (timing.isPacketEventTiming()) {
        packetTimings.add(timingJson);
      } else if (timing.isBukkitEventTiming()) {
        eventTimings.add(timingJson);
      } else {
        intaveTimings.add(timingJson);
      }
    }
    json.add("packet", packetTimings);
    json.add("event", eventTimings);
    json.add("intave", intaveTimings);
    return json;
  }

  @Override
  public void reset() {
    // not working
  }

  @Override
  public Set<DataCategory> categorySet() {
    return ImmutableSet.of(USAGE);
  }
}
