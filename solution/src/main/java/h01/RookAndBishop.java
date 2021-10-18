package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import java.util.concurrent.ThreadLocalRandom;

import static fopbot.Direction.*;
import static fopbot.Direction.LEFT;

public class RookAndBishop {
  private final int NUMBER_OF_ROWS;
  private final int NUMBER_OF_COLUMNS;
  private final int nextFrameDelay;
  private final boolean uiVisible;

  public RookAndBishop(int rows, int columns, int nextFrameDelay, boolean uiVisible) {
    this.nextFrameDelay = nextFrameDelay;
    this.uiVisible = uiVisible;
    this.NUMBER_OF_ROWS = rows > 0 ? rows : Task1.readProperty("NUMBER_OF_ROWS", Task1.TO_INTEGER);
    this.NUMBER_OF_COLUMNS = columns > 0 ? columns : Task1.readProperty("NUMBER_OF_COLUMNS", Task1.TO_INTEGER);
  }

  public RookAndBishop(int nextFrameDelay, boolean uiVisible) {
    this(-1, -1, nextFrameDelay, uiVisible);
  }

  public RookAndBishop() {
    this(20, true);
  }

  public void execute() {
    Task1.initializeTask(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS, nextFrameDelay, uiVisible);
    var coins = ThreadLocalRandom.current().nextInt(12, 21);
    Robot rook = new Robot(ThreadLocalRandom.current().nextInt(NUMBER_OF_COLUMNS),
      ThreadLocalRandom.current().nextInt(NUMBER_OF_ROWS),
      toDirection(ThreadLocalRandom.current().nextInt(4)), coins);
    Robot bishop = new Robot(ThreadLocalRandom.current().nextInt(NUMBER_OF_COLUMNS),
      ThreadLocalRandom.current().nextInt(NUMBER_OF_ROWS),
      toDirection(ThreadLocalRandom.current().nextInt(4)), 0);
    while (true) {
      rookMovement(rook);
      bishopMovement(rook, bishop);
      if (!rook.hasAnyCoins()) {
        System.out.println("Der Turm hat gewonnen!");
        break;
      }
      if (bishop.getX() == rook.getX() && bishop.getY() == rook.getY()) {
        System.out.println("Der Läufer hat gewonnen!");
        break;
      }
    }
  }

  static Direction toDirection(int value) {
    switch (value) {
      case 0:
        return UP;
      case 1:
        return RIGHT;
      case 2:
        return DOWN;
      case 3:
        return LEFT;
      default:
        throw new IllegalArgumentException("Für Eingabe außerhalb von {0, 1, 2, 3} gibt es keine Richtung");
    }
  }

  static boolean canMove(Robot robot) {
    switch (robot.getDirection()) {
      case UP:
        return robot.getY() != World.getHeight() - 1;
      case RIGHT:
        return robot.getX() != World.getWidth() - 1;
      case DOWN:
        return robot.getY() != 0;
      case LEFT:
        return robot.getX() != 0;
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Exercise 3.1 of H01
   */
  private void rookMovement(Robot rook) {
    rook.putCoin();
    int turn = ThreadLocalRandom.current().nextInt(4);
    if (canMove(rook)) {
      rook.move();
    } else {
      // if only for clarity
      if (turn > 1) {
        rook.turnLeft();
        rook.turnLeft();
      }
    }
    if (turn == 1) rook.turnLeft();
    if (turn == 0) {
      rook.turnLeft();
      rook.turnLeft();
      rook.turnLeft();
    }
  }


  /**
   * Exercise 3.2 of H01
   */
  private void bishopMovement(Robot rook, Robot bishop) {
    boolean notFinished = true;
    if (bishop.getX() == rook.getX() && bishop.getY() == rook.getY()) {
      notFinished = false;
    }
    while (notFinished) {
      if (canMove(bishop)) {
        bishop.move();
        bishop.turnLeft();
        if (canMove(bishop)) {
          bishop.move();
          bishop.turnLeft();
          bishop.turnLeft();
          bishop.turnLeft();
        } else {
          bishop.turnLeft();
          bishop.turnLeft();
          notFinished = false;
        }
      } else {
        bishop.turnLeft();
        notFinished = false;
      }
      if (bishop.isNextToACoin()) {
        bishop.pickCoin();
        notFinished = false;
      }
      if (bishop.getX() == rook.getX() && bishop.getY() == rook.getY()) {
        notFinished = false;
      }
    }
  }
}
