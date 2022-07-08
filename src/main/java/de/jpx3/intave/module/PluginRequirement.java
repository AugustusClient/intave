package de.jpx3.intave.module;

import org.bukkit.Bukkit;

import java.util.Arrays;

final class PluginRequirement implements Requirement {
  private final String[] plugins;

  PluginRequirement(String... plugins) {
    this.plugins = plugins;
  }

  @Override
  public boolean fulfilled() {
    if (plugins == null || plugins.length == 0) {
      return true;
    }
    return Arrays.stream(plugins)
      .allMatch(dependency -> Bukkit.getPluginManager().isPluginEnabled(dependency));
  }
}
