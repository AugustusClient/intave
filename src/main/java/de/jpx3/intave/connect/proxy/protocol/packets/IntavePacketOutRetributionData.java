package de.jpx3.intave.connect.proxy.protocol.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.connect.proxy.protocol.IntavePacket;

import java.util.UUID;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public class IntavePacketOutRetributionData extends IntavePacket {

  private UUID playerId;
  private String checkName;
  private String checkCategory;
  private String finalFlagMessage;
  private int finalTotalViolationLevel;

  public IntavePacketOutRetributionData() {
  }

  public IntavePacketOutRetributionData(UUID playerId, String checkName, String checkCategory, String finalFlagMessage, int finalTotalViolationLevel) {
    this.playerId = playerId;
    this.checkName = checkName;
    this.checkCategory = checkCategory;
    this.finalFlagMessage = finalFlagMessage;
    this.finalTotalViolationLevel = finalTotalViolationLevel;
  }

  @Override
  public void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError {
    playerId = UUID.fromString(input.readUTF());
    checkName = input.readUTF();
    checkCategory = input.readUTF();
    finalFlagMessage = input.readUTF();
    finalTotalViolationLevel = input.readInt();
  }

  @Override
  public void applyTo(ByteArrayDataOutput output) {
    output.writeUTF(playerId.toString());
    output.writeUTF(checkName);
    output.writeUTF(checkCategory);
    output.writeUTF(finalFlagMessage);
    output.writeInt(finalTotalViolationLevel);
  }
}
