package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.MetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.RotationUtilities;
import de.jpx3.intave.user.*;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

import static de.jpx3.intave.event.packet.PacketId.Client.*;

public final class PacketDependenciesHeuristic extends MetaCheckPart<Heuristics, PacketDependenciesHeuristic.PacketDependentHeuristicMeta> {
  private final IntavePlugin plugin;

  /*
  What the check does:

  The check should measure the time diffrences between multiple packets which are send by the client (measured in ticks / movement packets)
  The time diffrences betweeen theses packets then should be put into a standard deviation method which should give dependencies between theses packets
  when the returned value is pretty low.

  Some packets could false flagg which should then can be blacklisted manually.

  Example:
  Current Tick      PacketType
  1                   HELD_ITEM_SLOT ----|
  2                                      |-> time diffrence is 2
  3                   BLOCK_PLACE -------|
  4                   HELD_ITEM_SLOT -------|
  5                                         |-> time diffrence is 2
  6                   BLOCK_PLACE ----------|
  7                   HELD_ITEM_SLOT ----|
  8                                      |-> time diffrence is 2
  9                   BLOCK_PLACE -------|

  calculateStandardDeviation(2, 2, 2) was dann 0 ergeben sollte
   */
  public PacketDependenciesHeuristic(Heuristics parentCheck) {
    super(parentCheck, PacketDependenciesHeuristic.PacketDependentHeuristicMeta.class);
    this.plugin = IntavePlugin.singletonInstance();
  }

  private static List<Integer> diffrences = new ArrayList<>();

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      POSITION, POSITION_LOOK, FLYING, LOOK
    }
  )
  public void receiveMovement(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    PacketDependentHeuristicMeta meta = metaOf(user);
    player.sendMessage("-----------------------");

    for (Map.Entry<PacketType, ArrayList<Integer>> firstIntegerArrayListEntry : meta.packetTypeList.entrySet()) {
      PacketType firstPacketType = firstIntegerArrayListEntry.getKey();
      ArrayList<Integer> firstTicks = firstIntegerArrayListEntry.getValue();

      for (Map.Entry<PacketType, ArrayList<Integer>> secondIntegerArrayListEntry : meta.packetTypeList.entrySet()) {
        PacketType secondPacketType = secondIntegerArrayListEntry.getKey();
        ArrayList<Integer> secondTicks = secondIntegerArrayListEntry.getValue();

        diffrences.clear();
        if(firstPacketType != secondPacketType && firstTicks.size() > 5 && secondTicks.size() > 5) {
          for (int firstTick : firstTicks) {
            for (int secondTick : secondTicks) {
              if(firstTick < secondTick) {
                int diffrence = secondTick - firstTick;

                if(diffrence < 60) {
                  diffrences.add(diffrence);
                }
              }
            }
          }
        }

        if(diffrences.size() > 9) {
          double standardDeviation = RotationUtilities.calculateStandardDeviation(diffrences);
          player.sendMessage("std: " + MathHelper.formatDouble(standardDeviation, 4) +
            ChatColor.GREEN + " " + firstPacketType.name().toLowerCase() +
                              " " + secondPacketType.name().toLowerCase() +
            ChatColor.RESET + " " + diffrences.size());
        }

        if(firstTicks.size() > 10) {
          firstTicks.remove(0);
        }
        if(secondTicks.size() > 10) {
          secondTicks.remove(0);
        }
      }
    }

    prepareNextTick(meta);
  }

  private void addTickToPacketTypeList(PacketDependentHeuristicMeta meta, PacketType packetType) {
    ArrayList<Integer> ticks = meta.packetTypeList.get(packetType);
    if(ticks == null) {
      ticks = new ArrayList<>();
      meta.packetTypeList.put(packetType, ticks);
    }
    ticks.add(meta.currentTick);
  }

  private void prepareNextTick(PacketDependentHeuristicMeta meta) {
    meta.currentTick++;

    if(meta.currentTick > 500) {
      meta.packetTypeList.remove(meta.currentTick - 500);
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      ENTITY_ACTION,
      USE_ENTITY,
      ARM_ANIMATION,
      BLOCK_DIG,
      BLOCK_PLACE,
      HELD_ITEM_SLOT
    }
  )
  public void receivePackets(PacketEvent event) {
    PacketDependentHeuristicMeta meta = metaOf(userOf(event.getPlayer()));
    addTickToPacketTypeList(meta, event.getPacketType());
  }

  public final static class PacketDependentHeuristicMeta extends CheckCustomMetadata {
    int currentTick;
    HashMap<PacketType, ArrayList<Integer>> packetTypeList = new HashMap<>();
  }
}