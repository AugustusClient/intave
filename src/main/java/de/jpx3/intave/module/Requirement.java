package de.jpx3.intave.module;

interface Requirement {
  boolean fulfilled();

  default Requirement and(Requirement other) {
    return () -> fulfilled() && other.fulfilled();
  }

  default Requirement or(Requirement other) {
    return () -> fulfilled() || other.fulfilled();
  }
}
