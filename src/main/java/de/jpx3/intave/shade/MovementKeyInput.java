package de.jpx3.intave.shade;

public final class MovementKeyInput {
  private static final MovementKeyInput[][] UNIVERSE = new MovementKeyInput[3][3];
  public static final MovementKeyInput INVALID = new MovementKeyInput(-2, -2);

  static {
    for (int i = -1; i <= 1; i++) {
      MovementKeyInput[] strafeInputs = new MovementKeyInput[3];
      for (int j = -1; j <= 1; j++) {
        strafeInputs[j + 1] = new MovementKeyInput(i, j);
      }
      UNIVERSE[i + 1] = strafeInputs;
    }
  }

  private final int forward, strafe;

  private MovementKeyInput(int forward, int strafe) {
    this.forward = forward;
    this.strafe = strafe;
  }

  public int forward() {
    return forward;
  }

  public int strafe() {
    return strafe;
  }

  public float moveForward() {
    return forward * 0.98f;
  }

  public float strafeForward() {
    return strafe * 0.98f;
  }

  public MovementKeyInput clear() {
    return fromKeys(0, 0);
  }

  public static MovementKeyInput fromKeys(int forward, int strafe) {
    if (Math.abs(forward) > 1 || Math.abs(strafe) > 1) {
      return INVALID;
    }
    return UNIVERSE[forward + 1][strafe + 1];
  }
}