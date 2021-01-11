package de.jpx3.intave.connect.proxy.protocol;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * Class generated using IntelliJ IDEA
 * Any distribution is strictly prohibited.
 * Copyright Richard Strunk 2019
 */

public abstract class IntavePacket {
  public abstract void applyFrom(ByteArrayDataInput input) throws IllegalStateException, AssertionError;

  public abstract void applyTo(ByteArrayDataOutput output);
}
