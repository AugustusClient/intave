package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.event.punishment.AttackNerfStrategy;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

public final class NoSwingHeuristic extends IntaveMetaCheckPart<Heuristics, NoSwingHeuristic.NoSwingMeta> {

  public NoSwingHeuristic(Heuristics parentCheck) {
    super(parentCheck, NoSwingMeta.class);
  }

  @BukkitEventSubscription(priority = EventPriority.LOWEST)
  public void on(PlayerAnimationEvent swing) {
    Player player = swing.getPlayer();
    metaOf(player).swungHand = true;
  }

  @BukkitEventSubscription(priority = EventPriority.LOWEST)
  public void on(EntityDamageByEntityEvent event) {
    Entity attacker = event.getDamager();
    Entity damaged = event.getEntity();

    if (attacker instanceof Player && damaged instanceof LivingEntity &&
        event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
    ) {
      Player player = (Player) attacker;
      User user = userOf(player);
      NoSwingMeta meta = metaOf(user);
      if(!meta.swungHand) {
        String details = "missing swing packet on attack";
        Anomaly anomaly = Anomaly.anomalyOf("171", /*Confidence.LIKELY*/Confidence.NONE, Anomaly.Type.KILLAURA, details, Anomaly.AnomalyOption.LIMIT_4);
        parentCheck().saveAnomaly(player, anomaly);
        user.applyAttackNerfer(AttackNerfStrategy.CANCEL);
        event.setCancelled(true);
      }
      meta.swungHand = false;
    }
  }


  public static class NoSwingMeta extends UserCustomCheckMeta {
    private boolean swungHand = true;
  }
}
