package de.jpx3.intave.connect.proxy;

import de.jpx3.intave.connect.proxy.protocol.IntavePacket;
import org.bukkit.entity.Player;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public interface IntavePacketSubscription<T extends IntavePacket> {
  void onIncomingPacket(Player sender, T packet);
}
