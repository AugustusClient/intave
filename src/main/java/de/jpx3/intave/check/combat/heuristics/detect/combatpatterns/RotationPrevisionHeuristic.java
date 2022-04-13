package de.jpx3.intave.check.combat.heuristics.detect.combatpatterns;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.check.MetaCheckPart;
import de.jpx3.intave.check.combat.Heuristics;
import de.jpx3.intave.check.combat.heuristics.detect.clickpatterns.SwingBlueprintMeta;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.tracker.entity.EntityShade;
import de.jpx3.intave.shade.ClientMathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;

public final class RotationPrevisionHeuristic extends MetaCheckPart<Heuristics, RotationPrevisionHeuristic.RotationPrevisionMeta> {
  public RotationPrevisionHeuristic(Heuristics parentCheck) {
    super(parentCheck, RotationPrevisionMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      FLYING, LOOK, POSITION, POSITION_LOOK
    }
  )
  public void clientTickUpdate(PacketEvent event) {
    User user = userOf(event.getPlayer());
    Player player = user.player();
    RotationPrevisionMeta meta = metaOf(user);
    EntityShade target = user.meta().attack().lastAttackedEntity();
    if (target == null) {
      return;
    }
    MovementMetadata movementData = user.meta().movement();
    //user.player().sendMessage(String.format("x=%.3f y=%.3f z=%.3f",
    //  target.position.posX, target.position.posY, target.position.posZ));
    float lastPlayerYaw = ClientMathHelper.wrapAngleTo180_float(movementData.lastRotationYaw);
    float playerYaw = ClientMathHelper.wrapAngleTo180_float(movementData.rotationYaw);
    float serverYaw = resolveYawRotation(target.position, movementData.lastPositionX, movementData.lastPositionZ);

    float expectedYawDelta = MathHelper.distanceInDegrees(serverYaw, lastPlayerYaw);
    float yawDelta = MathHelper.distanceInDegrees(playerYaw, lastPlayerYaw);
    double diff = Math.abs(expectedYawDelta - yawDelta);
    //player.sendMessage(String.format("%.4f %.4f §bdiff: %.3f", expectedYawDelta, yawDelta, diff));
    if (meta.lastAttack <= 0) {
      RotationData rotationData = new RotationData(yawDelta, expectedYawDelta, 0, 0);
      meta.rotationDataList.add(rotationData);
      if (meta.rotationDataList.size() >= 100) {
        meta.rotationDataList.forEach(rotationData1 -> {
          //Bukkit.getConsoleSender().sendMessage(String.format("%.4f- %.4f",
           // rotationData1.expectedYawDelta, rotationData1.yawDelta));
        });
        meta.rotationDataList.clear();
      }
    }
    meta.lastAttack++;
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      USE_ENTITY
    }
  )
  public void clientUseEntity(PacketEvent event) {
    User user = userOf(event.getPlayer());
    RotationPrevisionMeta meta = metaOf(user);
    PacketContainer packet = event.getPacket();
    EnumWrappers.EntityUseAction action = packet.getEntityUseActions().readSafely(0);
    if (action == null) {
      action = packet.getEnumEntityUseActions().read(0).getAction();
    }
    if (action == EnumWrappers.EntityUseAction.ATTACK) {
      meta.lastAttack = 0;
    }
  }

  private float resolveYawRotation(EntityShade.EntityPositionContext entityPositions, double posX, double posZ) {
    final double diffX = entityPositions.posX - posX;
    final double diffZ = entityPositions.posZ - posZ;
    return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
  }

  public static class RotationData {
    protected final float yawDelta;
    protected final float expectedYawDelta;
    protected final float pitchDelta;
    protected final float expectedPitchDelta;

    public RotationData(float yawDelta, float expectedYawDelta, float pitchDelta, float expectedPitchDelta) {
      this.yawDelta = yawDelta;
      this.expectedYawDelta = expectedYawDelta;
      this.pitchDelta = pitchDelta;
      this.expectedPitchDelta = expectedPitchDelta;
    }
  }

  public final static class RotationPrevisionMeta extends CheckCustomMetadata {
    private final List<RotationData> rotationDataList = new ArrayList<>();
    private int lastAttack;
  }
}
