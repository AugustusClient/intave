package de.jpx3.intave.filter;

import de.jpx3.intave.detect.EventProcessor;

public class Filter implements EventProcessor {
  private final String name;

  public Filter(String name) {
    this.name = name;
  }

  protected boolean enabled() {
    return true;
  }
}
