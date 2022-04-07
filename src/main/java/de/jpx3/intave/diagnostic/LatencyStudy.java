package de.jpx3.intave.diagnostic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class LatencyStudy {
  private final static Map<Short, AtomicLong> hitDelays = new ConcurrentHashMap<>();

  private static long transPingCount;
  private static long transPingNum;

  public static void enterHit(short tickLatency) {
    hitDelays.computeIfAbsent(tickLatency, x -> new AtomicLong(0L)).incrementAndGet();
  }

  public static double average() {
    AtomicLong score = new AtomicLong();
    AtomicLong count = new AtomicLong();
    hitDelays.forEach((aShort, atomicLong) -> {
      score.addAndGet(aShort * atomicLong.get());
      count.addAndGet(atomicLong.get());
    });
    return (double) score.get() / Math.max((double) count.get(), 1);
  }

  public static void receivedTransactionAfter(long milliseconds) {
    transPingNum += Math.min(milliseconds, 1000);
    transPingCount++;

    if (transPingNum > Integer.MAX_VALUE / 2) {
      transPingNum /= 2;
      transPingCount /= 2;
    }
  }

  public static long transactionPingAverage() {
    return transPingNum == 0 ? 0 : transPingNum / transPingCount;
  }

  private static double cachedAverage;
  private static long lastCachedAverageReset;

  private static final long CACHE_EXPIRY = TimeUnit.SECONDS.toMillis(5);

  public static double cachedAverage() {
    if (System.currentTimeMillis() - lastCachedAverageReset > CACHE_EXPIRY) {
      cachedAverage = average();
      lastCachedAverageReset = System.currentTimeMillis();
    }
    return cachedAverage;
  }
}
