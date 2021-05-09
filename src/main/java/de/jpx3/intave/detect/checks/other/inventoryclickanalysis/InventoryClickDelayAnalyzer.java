package de.jpx3.intave.detect.checks.other.inventoryclickanalysis;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.adapter.ProtocolLibraryAdapter;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.other.InventoryClickAnalysis;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.event.service.violation.Violation;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.RotationMathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InventoryClickDelayAnalyzer extends IntaveMetaCheckPart<InventoryClickAnalysis, InventoryClickDelayAnalyzer.ClickDelayMeta> {
  private final IntavePlugin plugin;
  private final boolean invalidVersion;

  public InventoryClickDelayAnalyzer(InventoryClickAnalysis parentCheck) {
    super(parentCheck, ClickDelayMeta.class);
    invalidVersion = ProtocolLibraryAdapter.serverVersion().isAtLeast(MinecraftVersions.VER1_11_0);
    plugin = IntavePlugin.singletonInstance();
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "WINDOW_CLICK")
    }
  )
  public void windowClickPacket(PacketEvent event) {
    Player player = event.getPlayer();
    if (player.getGameMode().equals(GameMode.CREATIVE) || invalidVersion) {
      return;
    }

    User user = userOf(player);
    ClickDelayMeta meta = metaOf(user);

    int slot = event.getPacket().getIntegers().read(1);
    ItemStack itemStack = event.getPacket().getItemModifier().read(0);
    int clickedItemID = itemStack.getData().getItemTypeId() * 16 + itemStack.getData().getData();

//    for(Object object : event.getPacket().getModifier().getValues()) {
//      String s;
//      if(object == null) {
//        s = "null";
//      } else {
//        s = "" + object + " " + object.getClass();
//      }
//
//      player.sendMessage(s);
//    }
    boolean droppedAnItem = event.getPacket().getIntegers().read(3) == 4;

    if (slot != -999 && meta.lastClickedSlot != -999) {
//      player.sendMessage("" + droppedAnItem + " " + clickedItemID + " " + meta.lastClickedItemID);
      if ((clickedItemID != meta.lastClickedItemID || droppedAnItem) && meta.lastClickedTimeStamp != 0) {
        double distance = distanceBetween(slot, meta.lastClickedSlot);
        double time = (System.nanoTime() - meta.lastClickedTimeStamp) / 1000000000d;
        double speedAttr = distance / time;

        if (time < 2) {
          meta.clickDelayList.add(time);
        }

        if (meta.clickDelayList.size() > 15) {
          double std = RotationMathHelper.calculateStandardDeviation(meta.clickDelayList) * 100;
//          player.sendMessage("sdt " + new BigDecimal(std).setScale(6, RoundingMode.HALF_UP).toPlainString());

          if (std < 2) {
//            Violation violation = Violation.builderFor(InventoryClickAnalysis.class)
//              .withPlayer(player).withDefaultThreshold()
//              .withMessage("is switching too even between item slots")
//              .withDetails("moved between slots " + std)
//              .withVL(time > 0.01 ? 10 : 5).build();
//
//            //ViolationContext violationContext =
//            plugin.violationProcessor().processViolation(violation);
          }
          meta.clickDelayList.clear();
        }

        boolean flag = speedAttr > 30;
        boolean flag2 = speedAttr > 100;

        if (distance > 2 && flag && (flag2 || AccessHelper.now() - meta.lastFlagTimeStamp < 5000)) {
          Violation violation = Violation.builderFor(InventoryClickAnalysis.class)
            .withPlayer(player).withDefaultThreshold()
            .withMessage("is switching too quickly between item slots")
            .withDetails("moved from slot " + meta.lastClickedSlot + " to slot " + slot + " in " + MathHelper.formatDouble(time, 3) + " seconds")
            .withVL(time > 0.01 ? 10 : 5).build();

          //ViolationContext violationContext =
          plugin.violationProcessor().processViolation(violation);
//          if (IntaveControl.GOMME_MODE) {
//            if (distance > 0 && violationContext.violationLevelAfter() > 30) {
//              userOf(player).applyAttackNerfer(AttackNerfStrategy.DMG_MEDIUM);
//            }
//          }
        }

        if (flag) {
          meta.lastFlagTimeStamp = AccessHelper.now();
        }
      }
    }
    prepareNextTick(user, slot, clickedItemID);
  }

  private void prepareNextTick(User user, int slot, int itemID) {
    ClickDelayMeta meta = metaOf(user);
    meta.lastClickedSlot = slot;
    meta.lastClickedTimeStamp = System.nanoTime();
    meta.lastClickedItemID = itemID;
  }

  private double distanceBetween(int slot1, int slot2) {
    int[] slot1XZ = translatePosition(slot1);
    int[] slot2XZ = translatePosition(slot2);
    return Math.sqrt((slot1XZ[0] - slot2XZ[0]) * (slot1XZ[0] - slot2XZ[0]) + (slot1XZ[1] - slot2XZ[1]) * (slot1XZ[1] - slot2XZ[1]));
  }

  private int[] translatePosition(int slot) {
    int row = (slot / 9) + 1;
    int rowPosition = slot - ((row - 1) * 9);
    return new int[]{row, rowPosition};
  }

  public static final class ClickDelayMeta extends UserCustomCheckMeta {
    List<Double> clickDelayList = new ArrayList<>();
    private int lastClickedSlot;
    private long lastClickedTimeStamp;
    private int lastClickedItemID;
    private long lastFlagTimeStamp;
  }
}