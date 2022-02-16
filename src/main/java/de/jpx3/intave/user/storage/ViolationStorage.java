package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.module.violation.Violation;
import de.jpx3.intave.module.violation.ViolationContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ViolationStorage implements Storage {
  private final static long VIOLATION_UPDATE_CHECK_TIMEOUT = TimeUnit.MINUTES.toMillis(3);
  private final static long VIOLATION_INSERT_CHECK_COOLDOWN = TimeUnit.MINUTES.toMillis(30);
  private final static long VIOLATION_ALLOWED_LIFETIME = TimeUnit.DAYS.toMillis(7);
  private final static long VIOLATION_OVERALL_LIMIT = 256;

  private ViolationEvents interestingViolations = new ViolationEvents();

  public void noteViolation(ViolationContext violationContext) {
    Violation violation = violationContext.violation();
    String checkName = violation.check().name();
    String details = violation.details();
    int violationLevelAfter = (int) violationContext.violationLevelAfter();
    if (interestingViolation(checkName, details, violationLevelAfter)) {
      Optional<ViolationEvent> lastViolationEvent = lastViolationOfCheck(checkName);
      long lastViolationOfCheck = lastViolationEvent.map(ViolationEvent::timePassedSince).orElseGet(System::currentTimeMillis);
      if (lastViolationOfCheck > VIOLATION_INSERT_CHECK_COOLDOWN) {
        if (interestingViolations.size() < VIOLATION_OVERALL_LIMIT) {
          ViolationEvent event = new ViolationEvent(
            checkName.toLowerCase(Locale.ROOT),
            details.toLowerCase(Locale.ROOT),
            IntavePlugin.version().toLowerCase(Locale.ROOT),
            violationLevelAfter,
            System.currentTimeMillis()
          );
          addViolationEvent(event);
        }
      } else if (lastViolationOfCheck < VIOLATION_UPDATE_CHECK_TIMEOUT && lastViolationEvent.isPresent()) {
        ViolationEvent violationEvent = lastViolationEvent.get();
        if (violationLevelAfter > violationEvent.violationLevel()) {
          violationEvent.setViolationLevel(violationLevelAfter);
          violationEvent.setTimestamp(System.currentTimeMillis());
        }
      }
    }
  }

  public boolean interestingViolation(String checkName, String description, int vl) {
    switch (checkName.toLowerCase(Locale.ROOT)) {
      case "attackraytrace":
        return vl > 100;
      case "heuristics":
        return description.contains("!");
      case "physics":
      case "placementanalysis":
        return vl > 500;
      default:
        return false;
    }
  }

  private Optional<ViolationEvent> lastViolationOfCheck(String check) {
    String finalCheck = check.toLowerCase(Locale.ROOT);
    return interestingViolations.stream()
      .filter(event -> event.checkName().equals(finalCheck))
      .max(Comparator.comparingLong(ViolationEvent::timestamp));
  }

  private void addViolationEvent(ViolationEvent event) {
    interestingViolations.add(event);
  }

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    clearOldViolations();
    interestingViolations.writeTo(output);
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    interestingViolations.readFrom(input);
    clearOldViolations();
  }

  private void clearOldViolations() {
    interestingViolations = interestingViolations.withoutViolationsOlderThan(
      VIOLATION_ALLOWED_LIFETIME, TimeUnit.MILLISECONDS
    );
  }

  public ViolationEvents violations() {
    return interestingViolations;
  }

  public static class ViolationEvents implements Storage, Iterable<ViolationEvent> {
    private final Collection<ViolationEvent> parent;

    public ViolationEvents() {
      this(new ArrayList<>());
    }

    public ViolationEvents(Collection<ViolationEvent> parent) {
      this.parent = parent;
    }

    public int size() {
      return parent.size();
    }

    public boolean isEmpty() {
      return size() == 0;
    }

    public ViolationEvent first() {
      Iterator<ViolationEvent> iterator = parent.iterator();
      return iterator.hasNext() ? iterator.next() : null;
    }

    public ViolationEvents withoutViolationsOlderThan(
      long value, TimeUnit unit
    ) {
      return filter(
        event -> System.currentTimeMillis() - event.timestamp > unit.toMillis(value)
      );
    }

    public double matchFactor(
      Predicate<ViolationEvent> predicate
    ) {
      return (double) numMatching(predicate) / size();
    }

    public long numMatching(
      Predicate<ViolationEvent> predicate
    ) {
      return stream().filter(predicate).count();
    }

    public ViolationEvents filter(
      Predicate<ViolationEvent> predicate
    ) {
      List<ViolationEvent> filtered = stream()
        .filter(predicate)
        .collect(Collectors.toList());
      return new ViolationEvents(filtered);
    }

    public Stream<ViolationEvent> stream() {
      return parent.stream();
    }

    @NotNull
    @Override
    public Iterator<ViolationEvent> iterator() {
      return parent.iterator();
    }

    @Override
    public void forEach(Consumer<? super ViolationEvent> action) {
      parent.forEach(action);
    }

    @Override
    public Spliterator<ViolationEvent> spliterator() {
      return parent.spliterator();
    }

    @Override
    public void writeTo(ByteArrayDataOutput output) {
      output.writeInt(size());
      for (ViolationEvent violation : this) {
        violation.writeTo(output);
      }
    }

    @Override
    public void readFrom(ByteArrayDataInput input) {
      int violations = input.readInt();
      for (int i = 0; i < violations; i++) {
        ViolationEvent violation = new ViolationEvent();
        violation.readFrom(input);
        add(violation);
      }
    }

    public void add(ViolationEvent event) {
      parent.add(event);
    }
  }

  public static class ViolationEvent implements Storage {
    private String checkName;
    private String details;
    private String intaveVersion;
    private int violationLevel;
    private long timestamp;

    public ViolationEvent() {
    }

    public ViolationEvent(
      String checkName,
      String details,
      String intaveVersion,
      int violationLevel,
      long timestamp
    ) {
      this.checkName = checkName;
      this.details = details;
      this.intaveVersion = intaveVersion;
      this.violationLevel = violationLevel;
      this.timestamp = timestamp;
    }

    @Override
    public void writeTo(ByteArrayDataOutput output) {
      output.writeUTF(checkName);
      output.writeUTF(details);
      output.writeUTF(intaveVersion);
      output.writeInt(violationLevel);
      output.writeLong(timestamp);
    }

    @Override
    public void readFrom(ByteArrayDataInput input) {
      checkName = input.readUTF();
      details = input.readUTF();
      intaveVersion = input.readUTF();
      violationLevel = input.readInt();
      timestamp = input.readLong();
    }

    public String checkName() {
      return checkName;
    }

    public String details() {
      return details;
    }

    public String intaveVersion() {
      return intaveVersion;
    }

    public int violationLevel() {
      return violationLevel;
    }

    public void setViolationLevel(int violationLevel) {
      this.violationLevel = violationLevel;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }

    public long timePassedSince() {
      return System.currentTimeMillis() - timestamp;
    }

    public long timestamp() {
      return timestamp;
    }
  }
}
