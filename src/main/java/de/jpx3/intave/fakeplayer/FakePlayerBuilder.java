package de.jpx3.intave.fakeplayer;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Preconditions;
import de.jpx3.intave.fakeplayer.movement.types.Movement;
import org.bukkit.entity.Player;

import static de.jpx3.intave.fakeplayer.MojangGameProfileResolver.acquireGameProfile;

public final class FakePlayerBuilder {
  private Player parentPlayer = null;
  private int entityID = -1;
  private WrappedGameProfile wrappedGameProfile;
  private String tabListPrefix = "";
  private String prefix = "";
  private Movement movement = null;
  private boolean invisible = false;
  private boolean visibleInTablist = true;
  private boolean equipArmor = false;
  private boolean equipHeldItem = false;
  private FakePlayerAttackSubscriber fakePlayerAttackSubscriber = () -> {
  };

  FakePlayerBuilder() {
  }

  public FakePlayerBuilder withParentPlayer(Player player) {
    this.parentPlayer = player;
    return this;
  }

  public FakePlayerBuilder withEntityID(int entityID) {
    this.entityID = entityID;
    return this;
  }

  public FakePlayerBuilder withPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public FakePlayerBuilder withTabListPrefix(String tabListPrefix) {
    this.tabListPrefix = tabListPrefix;
    return this;
  }

  public FakePlayerBuilder withMovement(Movement movement) {
    this.movement = movement;
    return this;
  }

  public FakePlayerBuilder invisible() {
    this.invisible = true;
    return this;
  }

  public FakePlayerBuilder visible() {
    this.invisible = false;
    return this;
  }

  public FakePlayerBuilder invisibleInTabList() {
    this.visibleInTablist = false;
    return this;
  }

  public FakePlayerBuilder visibleInTabList() {
    this.visibleInTablist = true;
    return this;
  }

  public FakePlayerBuilder equipArmor() {
    this.equipArmor = true;
    return this;
  }

  public FakePlayerBuilder equipHeldItem() {
    this.equipHeldItem = true;
    return this;
  }

  public FakePlayerBuilder withAttackSubscriber(FakePlayerAttackSubscriber subscriber) {
    this.fakePlayerAttackSubscriber = subscriber;
    return this;
  }

  public FakePlayerBuilder withGameProfile(WrappedGameProfile gameProfile) {
    this.wrappedGameProfile = gameProfile;
    return this;
  }

  public FakePlayer build() {
    Preconditions.checkNotNull(this.parentPlayer);
    Preconditions.checkState(this.entityID >= 0, "EntityId can not be negative!");
    Preconditions.checkNotNull(this.movement);
    return new FakePlayer(
      this.movement,
      this.parentPlayer,
      this.wrappedGameProfile == null ? acquireGameProfile() : this.wrappedGameProfile,
      this.tabListPrefix,
      this.prefix,
      this.entityID,
      this.invisible,
      this.visibleInTablist,
      this.equipArmor,
      this.equipHeldItem,
      this.fakePlayerAttackSubscriber
    );
  }
}
