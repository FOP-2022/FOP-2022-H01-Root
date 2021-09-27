package H01;

import fopbot.*;
import H01.misc.PropertyConverter;
import H01.misc.PropertyException;

import java.io.*;
import java.util.Random;

import static fopbot.Direction.*;

/**
 * Task 1 of Hausuebung01 of "Funktionale und objektorientierte
 * Programmierkonzepte" WS 21/22. In this task students have to add
 * functionality to certain {@code Robot}s according to the sheets presented in
 * the lecture. This class initializes the world which has to be used and
 * presents which parts of code have to be changed. Depending on whether this is
 * the solution or the template the doc and included code might differ.
 * <p>
 * If this is the template do ONLY modify parts of the code which are commented
 * to be modified.
 *
 * @author Thomas Rothenbaecher
 */

public class Task1 {

  private static final PropertyConverter<Integer> TO_INTEGER = s -> {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new PropertyException("Die Property kann nicht als Ganzzahl interpretiert werden.", e);
    }
  };

  private static final String FOPBOT_PROPERTIES = "fopbot.properties";

  public static class RookAndBishop {
    private final int NUMBER_OF_ROWS;
    private final int NUMBER_OF_COLUMNS;
    private final int nextFrameDelay;
    private final boolean uiVisible;

    public RookAndBishop(int rows, int columns, int nextFrameDelay, boolean uiVisible) {
      this.nextFrameDelay = nextFrameDelay;
      this.uiVisible = uiVisible;
      this.NUMBER_OF_ROWS = rows > 0 ? rows : readProperty("NUMBER_OF_ROWS", TO_INTEGER);
      this.NUMBER_OF_COLUMNS = columns > 0 ? columns : readProperty("NUMBER_OF_COLUMNS", TO_INTEGER);
    }

    public RookAndBishop(int nextFrameDelay, boolean uiVisible) {
      this(-1, -1, nextFrameDelay, uiVisible);
    }

    public RookAndBishop() {
      this(20, true);
    }

    public void execute() {
      initializeTask(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS, nextFrameDelay, uiVisible);
      Random random = new Random();
      int coins = 0;
      while (coins < 12) {
        coins = random.nextInt(21);
      }
      Robot rook = new Robot(random.nextInt(NUMBER_OF_COLUMNS), random.nextInt(NUMBER_OF_ROWS),
        toDirection(random.nextInt(4)), coins);
      Robot bishop = new Robot(random.nextInt(NUMBER_OF_COLUMNS), random.nextInt(NUMBER_OF_ROWS),
        toDirection(random.nextInt(4)), 0);
      while (true) {
        rook.putCoin();
        int turn = random.nextInt(4);
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

  }
  /**
   * Method which makes Java code runnable as a program. For this task the main
   * method handles initialization and preparation of the world and anything
   * needed necessary to finish this task.
   *
   * @param args generic arguments needed by definition to run a program
   */
  public static void main(String[] args) {
    var environment = new RookAndBishop();
    environment.execute();
  }

  private static Direction toDirection(int value) {
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

  private static boolean canMove(Robot robot) {
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

  private static void initializeTask(int numberOfRows, int numberOfColumns, int delay, boolean uiVisible) {
    World.setSize(numberOfColumns, numberOfRows);
    World.setVisible(uiVisible);
    if (delay < 0) {
      delay = 100;
    }
    World.setDelay(delay);
  }

  private static <T> T readProperty(String key, PropertyConverter<T> converter) {
    String value = null;
    var loader = Task1.class.getClassLoader();
    try (InputStream inputStream = loader.getResourceAsStream(FOPBOT_PROPERTIES)) {
      if (inputStream == null) {
        throw new PropertyException(String.format("Die Property-Datei mit Namen %s " +
          "konnte nicht gefunden werden.", FOPBOT_PROPERTIES));
      }
      try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
           BufferedReader br = new BufferedReader(inputStreamReader)) {
        String line = null;
        while ((line = br.readLine()) != null) {
          if (line.startsWith(key)) {
            var split = line.split("=");
            try {
              value = split[1];
            } catch (ArrayIndexOutOfBoundsException e) {
              throw new PropertyException(String.format("Die Zeile für Schlüssel %s enthält kein '='", key), e);
            }
            break;
          }
        }
      }
    } catch (IOException e) {
      throw new PropertyException("Die Property-Datei konnte nicht gelesen werden.", e);
    }
    if (value == null) {
      throw new PropertyException(String.format("Der gesuchte Schlüssel %s konnte in der Datei nicht gefunden werden.",
        key));
    }
    return converter.convert(value);
  }
}
