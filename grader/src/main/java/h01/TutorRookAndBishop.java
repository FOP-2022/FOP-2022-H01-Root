package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import java.util.concurrent.ThreadLocalRandom;

import static fopbot.Direction.*;
import static fopbot.Direction.LEFT;

public class TutorRookAndBishop {

  private final RookAndBishop student;
  private final int width;
  private final int height;
  private Robot rook;
  private Robot bishop;

  public TutorRookAndBishop(RookAndBishop student, int width, int height, Robot rook, Robot bishop) {
    this.student = student;
    this.width = width;
    this.height = height;
    this.rook = rook;
    this.bishop = bishop;
  }

  static void initializeTask(int numberOfRows, int numberOfColumns) {
    World.reset();
    World.setSize(numberOfColumns, numberOfRows);
    World.setVisible(false);
    World.setDelay(0);
  }

  public void init() {
    initializeTask(height, width);
    var coins = ThreadLocalRandom.current().nextInt(12, 21);
    this.rook = new Robot(ThreadLocalRandom.current().nextInt(width),
      ThreadLocalRandom.current().nextInt(height),
      toDirection(ThreadLocalRandom.current().nextInt(4)), coins);
    this.bishop = new Robot(ThreadLocalRandom.current().nextInt(width),
      ThreadLocalRandom.current().nextInt(height),
      toDirection(ThreadLocalRandom.current().nextInt(4)), 0);
  }

  public void execute() {
    while (true) {
      try {
        var method = student.getClass().getDeclaredMethod("rookMovement", Robot.class);
        method.setAccessible(true);
        method.invoke(student, rook);
      } catch (Exception ignored) {
      }
      try {
        var method = student.getClass().getDeclaredMethod("bishopMovement", Robot.class);
        method.setAccessible(true);
        method.invoke(student, rook);
      } catch (Exception ignored) {
      }
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
    if (turn == 1) {
      rook.turnLeft();
    }
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
