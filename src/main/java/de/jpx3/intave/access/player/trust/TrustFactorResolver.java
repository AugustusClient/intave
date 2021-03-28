package de.jpx3.intave.access.player.trust;

import de.jpx3.intave.tools.annotate.Relocate;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Relocate
public interface TrustFactorResolver {
  void resolve(Player player, Consumer<TrustFactor> callback);
}
