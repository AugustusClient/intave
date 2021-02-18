package de.jpx3.intave.world.block;

import org.bukkit.Material;

public final class BlockTypeAccess {
  public static final Material WEB;

  static {
    Material legacyWeb = Material.getMaterial("WEB");
    WEB = legacyWeb != null ? legacyWeb : Material.getMaterial("COBWEB");
  }
}