package de.jpx3.intave.detect.checks.combat.heuristics;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaAttackData;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.entity.Player;

public final class ReverseSilentStrafeHeuristics extends IntaveMetaCheckPart<Heuristics, ReverseSilentStrafeHeuristics.ReverseSilentStrafeHeuristicMeta> {
  public ReverseSilentStrafeHeuristics(Heuristics parentCheck) {
    super(parentCheck, ReverseSilentStrafeHeuristicMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK")
    }
  )
  public void checkInvalidJump(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    ReverseSilentStrafeHeuristicMeta heuristicMeta = metaOf(user);
    UserMetaMovementData movementData = user.meta().movementData();

//    if (!checkable(user)) {
//      return;
//    }

    int moveForward = movementData.keyForward;
    int moveStrafe = movementData.keyStrafe;

//    player.sendMessage(PhysicsHelper.resolveKeysFromInput(moveForward, moveStrafe));

    if (movementData.lastTeleport > 5) {
      float rotationYaw = movementData.rotationYaw;
    }
  }

  private boolean checkable(User user) {
    UserMetaAttackData attackData = user.meta().attackData();
    Heuristics.HeuristicMeta heuristicMeta = parentCheck().metaOf(user);
    return heuristicMeta.overallAttacks > 50 && attackData.recentlyAttacked(1000) && AccessHelper.now() - heuristicMeta.firstAttack > 70_000;
  }

  public static final class ReverseSilentStrafeHeuristicMeta extends UserCustomCheckMeta {
    private int keyChanges = 0;
  }
}

