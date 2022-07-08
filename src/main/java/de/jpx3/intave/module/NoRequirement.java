package de.jpx3.intave.module;

final class NoRequirement implements Requirement {
  @Override
  public boolean fulfilled() {
    return true;
  }
}
