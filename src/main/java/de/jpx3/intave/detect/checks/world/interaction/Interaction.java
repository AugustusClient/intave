package de.jpx3.intave.detect.checks.world.interaction;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class Interaction {
  private final World world;
  private final Player player;
  private final BlockPosition targetBlock;
  private final int targetDirection;
  private final PacketContainer thePacket;
  private final InteractionType type;
  private final Material itemTypeInHand;
  private final EnumWrappers.Hand hand;
  private boolean ignoreFlags;
  private boolean isStartBlockBreak;
  private boolean entered = false;
  private boolean changeLocked = true;

  public Interaction(
    World world, Player player,
    BlockPosition targetBlock,
    int targetDirection,
    PacketContainer thePacket,
    InteractionType type,
    Material itemTypeInHand, EnumWrappers.Hand hand,
    boolean ignoreFlags,
    boolean isStartBlockBreak) {
    this.world = world;
    this.player = player;
    this.targetBlock = targetBlock;
    this.targetDirection = targetDirection;
    this.thePacket = thePacket;
    this.type = type;
    this.itemTypeInHand = itemTypeInHand;
    this.hand = hand;
    this.ignoreFlags = ignoreFlags;
    this.isStartBlockBreak = isStartBlockBreak;
  }

  public PacketContainer thePacket() {
    return thePacket;
  }

  public InteractionType type() {
    return type;
  }

  public World world() {
    return world;
  }

  public Player player() {
    return player;
  }

  public Material itemTypeInHand() {
    return itemTypeInHand;
  }

  public EnumWrappers.Hand hand() {
    return hand;
  }

  public void unlock() {
    changeLocked = false;
  }

  public boolean isChangeLocked() {
    return changeLocked;
  }

  public boolean isIgnoreFlags() {
    return ignoreFlags;
  }


  public BlockPosition targetBlock() {
    return targetBlock;
  }

  public int targetDirection() {
    return targetDirection;
  }

  public boolean isEntered() {
    return entered;
  }

  public void enter() {
    entered = true;
  }

  public boolean entered() {
    return entered;
  }

  public boolean isStartBlockBreak() {
    return isStartBlockBreak;
  }

  @Override
  public String toString() {
    return "Interaction{" +
      "targetBlock=" + targetBlock +
      ", targetDirection=" + targetDirection +
      ", type=" + type +
      ", itemTypeInHand=" + itemTypeInHand +
      ", hand=" + hand +
      ", entered=" + entered +
      '}';
  }
}
