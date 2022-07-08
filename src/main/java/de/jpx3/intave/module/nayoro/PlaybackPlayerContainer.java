package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.check.combat.heuristics.Confidence;
import de.jpx3.intave.module.mitigate.AttackNerfStrategy;
import de.jpx3.intave.module.nayoro.detection.DetectionSubscription;
import de.jpx3.intave.module.nayoro.event.AttackEvent;
import de.jpx3.intave.module.nayoro.event.PlayerInitEvent;
import de.jpx3.intave.module.nayoro.event.PlayerMoveEvent;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.share.Rotation;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import org.bukkit.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class PlaybackPlayerContainer extends SinkPlayerContainer {
  private Environment environment;
  private final Map<Class<?>, CheckCustomMetadata> metadata = new HashMap<>();
  private final DetectionSubscription detectionSubscription;
  private int id;
  private int version;
  private boolean outdated;
  private double posX;
  private double posY;
  private double posZ;
  private float yaw;
  private float pitch;
  private float lastYaw;
  private float lastPitch;

  private long lastAttack;

  public PlaybackPlayerContainer(DetectionSubscription... detectionSubscription) {
    this.detectionSubscription = DetectionSubscription.merge(detectionSubscription);
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public int version() {
    return version;
  }

  @Override
  public boolean outdatedClient() {
    return outdated;
  }

  @Override
  public <T extends CheckCustomMetadata> T meta(Class<T> metaClass) {
    // noinspection unchecked
    return (T) metadata.computeIfAbsent(metaClass, k -> {
      try {
        return metaClass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public Rotation rotation() {
    return new Rotation(yaw, pitch);
  }

  @Override
  public float yaw() {
    return yaw;
  }

  @Override
  public float pitch() {
    return pitch;
  }

  @Override
  public Rotation lastRotation() {
    return new Rotation(lastYaw, lastPitch);
  }

  @Override
  public float lastYaw() {
    return lastYaw;
  }

  @Override
  public float lastPitch() {
    return lastPitch;
  }

  @Override
  public Position position() {
    return new Position(posX, posY, posZ);
  }

  @Override
  public double x() {
    return posX;
  }

  @Override
  public double y() {
    return posY;
  }

  @Override
  public double z() {
    return posZ;
  }

  @Override
  public boolean cursorUponEntity(int id, float expansion) {
    return environment.inSight(id);
  }

  @Override
  public boolean notTeleportedIn(int ticks) {
    return false;
  }

  @Override
  public boolean inGameMode(GameMode gameMode) {
    return false;
  }

  @Override
  public boolean recentlyAttacked(long millis) {
    return environment.hasPassed(lastAttack + millis);
  }

  @Override
  public boolean recentlySwitchedEntity(long millis) {
    return false;
  }

  @Override
  public Environment environment() {
    return environment;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void debug(String message) {
    detectionSubscription.onDebug(message);
  }

  @Override
  public void nerf(AttackNerfStrategy strategy, String originCode) {
    detectionSubscription.onNerf(strategy, originCode);
  }

  @Override
  public void noteAnomaly(String key, Confidence confidence, String description) {
    detectionSubscription.onAnomaly(key, confidence, description);
  }

  @Override
  public void applyIfUserPresent(Consumer<User> action) {
    // ignore
  }

  @Override
  public void visit(AttackEvent event) {
    lastAttack = System.currentTimeMillis();
    visitAny(event);
  }

  @Override
  public void visit(PlayerInitEvent event) {
    id = event.id();
    version = event.version();
    outdated = event.outdated();
  }

  @Override
  public void visit(PlayerMoveEvent event) {
    lastYaw = yaw;
    lastPitch = pitch;
    if (event.applyX()) {
      this.posX = event.x();
    } else {
      event.setX(this.posX);
    }
    if (event.applyY()) {
      this.posY = event.y();
    } else {
      event.setY(this.posY);
    }
    if (event.applyZ()) {
      this.posZ = event.z();
    } else {
      event.setZ(this.posZ);
    }
    if (event.applyYaw()) {
      this.yaw = event.yaw();
    } else {
      event.setYaw(this.yaw);
    }
    if (event.applyPitch()) {
      this.pitch = event.pitch();
    } else {
      event.setPitch(this.pitch);
    }
    visitAny(event);
  }
}
