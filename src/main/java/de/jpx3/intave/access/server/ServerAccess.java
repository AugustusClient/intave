package de.jpx3.intave.access.server;

import de.jpx3.intave.tools.annotate.Relocate;

@Relocate
public interface ServerAccess {
  ServerHealthStatisticAccess health();
}
