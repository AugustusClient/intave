package de.jpx3.intave.connect.sibyl.data.packet;

import com.google.gson.JsonElement;

public abstract class SibylPacket {
  private final String packetName;

  protected SibylPacket(String packetName) {
    this.packetName = packetName;
  }

  public String packetName() {
    return packetName;
  }

  public abstract void buildFrom(JsonElement element);

  public abstract JsonElement asJsonElement();
}
