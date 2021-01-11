package de.jpx3.intave.connect.sibyl.data.packet;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public final class SibylPacketRegister {
  private static Map<String, Class<? extends SibylPacket>> availablePackets = new HashMap<>();
  static {
    availablePackets.put("out-attack-cancel", SibylPacketOutAttackCancel.class);

    availablePackets = ImmutableMap.copyOf(availablePackets);
  }

  public static <PACKET extends SibylPacket> PACKET createFrom(String label, JsonElement data) {
    PACKET generatedPacket = generatePacket(label);
    generatedPacket.buildFrom(data);
    return generatedPacket;
  }

  private static <PACKET extends SibylPacket> PACKET generatePacket(String label) {
    try {
      //noinspection unchecked
      return (PACKET) availablePackets.get(label).newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
