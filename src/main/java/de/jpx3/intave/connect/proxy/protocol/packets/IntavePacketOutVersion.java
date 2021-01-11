package de.jpx3.intave.connect.proxy.protocol.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import de.jpx3.intave.connect.proxy.protocol.IntavePacket;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public class IntavePacketOutVersion extends IntavePacket {

  private String intaveVersion;
  private int protocolVersion;

  public IntavePacketOutVersion() {
  }

  public IntavePacketOutVersion(String intaveVersion, int protocolVersion) {
    this.intaveVersion = intaveVersion;
    this.protocolVersion = protocolVersion;
  }

  @Override
  public void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError {
    intaveVersion = input.readUTF();
    protocolVersion = input.readInt();
  }

  @Override
  public void applyTo(ByteArrayDataOutput output) {
    output.writeUTF(intaveVersion);
    output.writeInt(protocolVersion);
  }
}
