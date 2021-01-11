package de.jpx3.intave.connect.proxy.protocol.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.connect.proxy.protocol.IntavePacket;

import java.util.Arrays;
import java.util.UUID;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public class IntavePacketOutPunishment extends IntavePacket {

  private UUID playerId;
  private PunishmentType punishmentType;
  private String message;
  private long tempbanEndTimestamp;

  public IntavePacketOutPunishment() {
  }

  public IntavePacketOutPunishment(UUID playerId, PunishmentType punishmentType, String message, long tempbanEndTimestamp) {
    this.playerId = playerId;
    this.punishmentType = punishmentType;
    this.message = message;
    this.tempbanEndTimestamp = tempbanEndTimestamp;
  }

  @Override
  public void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError {
    playerId = UUID.fromString(input.readUTF());
    punishmentType = PunishmentType.fromId(input.readInt());
    message = input.readUTF();
    tempbanEndTimestamp = input.readLong();
  }

  @Override
  public void applyTo(ByteArrayDataOutput output) {
    output.writeUTF(playerId.toString());
    output.writeInt(punishmentType.getTypeId());
    output.writeUTF(message);
    output.writeLong(tempbanEndTimestamp);
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public PunishmentType getPunishmentType() {
    return punishmentType;
  }

  public long getTempbanEndTimestamp() {
    return tempbanEndTimestamp;
  }

  public String getMessage() {
    return message;
  }

  public enum PunishmentType {
    KICK(1),
    TEMP_BAN(2),
    BAN(3);

    private final int typeId;

    PunishmentType(int typeId) {
      this.typeId = typeId;
    }

    public static PunishmentType fromId(int id) {
      return Arrays.stream(PunishmentType.values())
        .filter(value -> value.getTypeId() == id)
        .findFirst()
        .orElse(null);
    }

    public int getTypeId() {
      return typeId;
    }
  }
}
