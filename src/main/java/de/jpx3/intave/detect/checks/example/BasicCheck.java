package de.jpx3.intave.detect.checks.example;

import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.entity.Player;

public final class BasicCheck extends IntaveMetaCheck<BasicCheck.MyCustomMeta> {
  public BasicCheck(String checkName, String configurationName) {
    super(checkName, configurationName, MyCustomMeta.class);
  }

  public void test(Player player) {
    MyCustomMeta myCustomMeta = metaOf(player);
    myCustomMeta.test = false;
  }

  public static class MyCustomMeta extends UserCustomCheckMeta {
    public boolean test;
  }
}
