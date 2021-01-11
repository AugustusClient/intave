package de.jpx3.intave.connect.sibyl.data.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.util.Vector;

import java.util.UUID;

public final class SibylPacketOutAttackCancel extends SibylPacket {
  private UUID attacker;
  private Vector attackedLocation;
  private boolean damage;

  public SibylPacketOutAttackCancel() {
    super("out-attack-cancel");
  }

  public UUID attacker() {
    return attacker;
  }

  public void setAttacker(UUID attacker) {
    this.attacker = attacker;
  }

  public Vector attackedLocation() {
    return attackedLocation;
  }

  public void setAttackedLocation(Vector attackedLocation) {
    this.attackedLocation = attackedLocation;
  }

  public boolean isDamage() {
    return damage;
  }

  public void setDamage(boolean damage) {
    this.damage = damage;
  }

  @Override
  public void buildFrom(JsonElement element) {
    JsonObject jsonObject = element.getAsJsonObject();
    attacker = UUID.fromString(jsonObject.get("attackerId").getAsString());
    attackedLocation = new Vector();
    attackedLocation.setX(jsonObject.get("attackedPositionX").getAsDouble());
    attackedLocation.setY(jsonObject.get("attackedPositionY").getAsDouble());
    attackedLocation.setZ(jsonObject.get("attackedPositionZ").getAsDouble());
    damage = jsonObject.get("damage").getAsBoolean();
  }

  @Override
  public JsonElement asJsonElement() {
    JsonObject object = new JsonObject();
    object.addProperty("attackerId", attacker.toString());
    object.addProperty("attackedPositionX", attackedLocation.getX());
    object.addProperty("attackedPositionY", attackedLocation.getY());
    object.addProperty("attackedPositionZ", attackedLocation.getZ());
    object.addProperty("damage", damage);
    return object;
  }
}
