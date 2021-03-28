package de.jpx3.intave.access;

import de.jpx3.intave.tools.annotate.Relocate;

@Relocate
public final class IntaveColdException extends IntaveAccessException {
  public IntaveColdException() {
    super();
  }

  public IntaveColdException(String message) {
    super(message);
  }

  public IntaveColdException(String message, Throwable cause) {
    super(message, cause);
  }

  public IntaveColdException(Throwable cause) {
    super(cause);
  }
}
