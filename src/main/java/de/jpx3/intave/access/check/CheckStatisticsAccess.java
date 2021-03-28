package de.jpx3.intave.access.check;

import de.jpx3.intave.tools.annotate.Relocate;

@Relocate
public interface CheckStatisticsAccess {
  long totalProcesses();
  long totalPasses();
  @Deprecated
  long totalViolations();
  @Deprecated
  long totalFails();
}
