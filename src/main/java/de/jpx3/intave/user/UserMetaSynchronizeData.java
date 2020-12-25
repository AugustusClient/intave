package de.jpx3.intave.user;

import com.google.common.collect.Maps;
import de.jpx3.intave.event.service.transaction.TransactionCallBackData;

import java.util.Map;

public final class UserMetaSynchronizeData {
  private final Map<Short, TransactionCallBackData<?>> transactionFeedBackMap = Maps.newHashMap();
  public short transactionCounter;

  public Map<Short, TransactionCallBackData<?>> transactionFeedBackMap() {
    return transactionFeedBackMap;
  }
}