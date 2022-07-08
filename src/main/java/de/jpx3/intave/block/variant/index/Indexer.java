package de.jpx3.intave.block.variant.index;

import org.bukkit.Material;

import java.util.Map;

interface Indexer {
  Map<Object, Integer> index(Material type);
}
