package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.packet.converter.PlayerInfoDataConverter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class PlayerInfoReader extends AbstractPacketReader {
  private static final boolean MULTIPLE_ACTIONS = MinecraftVersions.VER1_19_3.atOrAbove();

  public Set<EnumWrappers.PlayerInfoAction> playerInfoActions() {
    if (MULTIPLE_ACTIONS) {
      return packet().getPlayerInfoActions().read(0);
    } else {
      return Collections.singleton(packet().getPlayerInfoAction().read(0));
    }
  }

  public List<PlayerInfoData> playerInfoData() {
//    return packet().getPlayerInfoDataLists().read(0);
    return packet().getModifier().withType(List.class, PlayerInfoDataConverter.threadConverter()).read(0);
  }
}
