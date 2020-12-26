package de.jpx3.intave.user;

import de.jpx3.intave.tools.items.PlayerEnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class UserMetaInventoryData {
  private final Player player;
  private ItemStack heltItem;
  private boolean handActive;

  private boolean inventoryOpen;
  public int handActiveTicks;
  public int pastItemUsageTransition;
  public int pastHotBarSlotChange;
  public int selectedHotBarSlot;

  public UserMetaInventoryData(Player player) {
    this.player = player;
    this.heltItem = resolveMaterialInHand();
  }

  public void resynchronizeHeldItem() {
    this.heltItem = resolveMaterialInHand();
  }

  private ItemStack resolveMaterialInHand() {
    return player == null ? null : player.getItemInHand();
  }

  public boolean handActive() {
    return handActive;
  }

  public ItemStack heldItem() {
    return heltItem;
  }

  public Material heldItemType() {
    return heltItem == null ? Material.AIR : heltItem.getType();
  }

  public boolean inventoryOpen() {
    return inventoryOpen;
  }

  public void setHeldItem(ItemStack heldItem) {
    this.heltItem = heldItem;
  }

  public void deactivateHand() {
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    if (heltItem != null && PlayerEnchantmentHelper.tridentRiptideEnchanted(heltItem)) {
      movementData.pastRiptideSpin = 0;
    }
    this.handActive = false;
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void activateHand() {
    this.handActive = true;
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void applySlotSwitch() {
    int previousItemSlot = this.selectedHotBarSlot;
    int newItemSlot = this.selectedHotBarSlot + 1;
    if (newItemSlot > 8) {
      newItemSlot = 0;
    }
    setHeldItemSlot(newItemSlot);
    setHeldItemSlot(previousItemSlot);
  }

  private void setHeldItemSlot(int slot) {
    if(player == null) {
      return;
    }
    PlayerInventory inventory = player.getInventory();
    inventory.setHeldItemSlot(slot);
  }

  public void setInventoryOpen(boolean inventoryOpen) {
    this.inventoryOpen = inventoryOpen;
  }
}