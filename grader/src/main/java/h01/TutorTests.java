package h01;

import fopbot.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Thomas Rothenbächer
 */
@TestForSubmission("h01")
public class TutorTests {
  private static final int RUNS = 100;
  private static final String BISHOP_WIN = "ufer";
  private static final String ROOK_WIN = "Turm";
  private static final double chi10percent3df = 6.251;
  private static final double chi2percent3df = 9.837;
  private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private static final PrintStream originalOut = System.out;
  static List<Integer> columns = new ArrayList<>(List.of(12, 10, 11, 1, 3, 5, 8, 7, 9, 13, 10, 11, 1, 13, 11, 7, 6));
  private static final int RUNS_WITH_TRANSITION = columns.size();
  private static final int RUNS_WITH_EIGHT = 30;
  static List<Integer> rows = new ArrayList<>(List.of(11, 10, 8, 1, 1, 13, 11, 7, 6, 10, 13, 11, 7, 10, 11, 1, 6));
  static List<Integer> eights = Stream.generate(() -> 8)
    .limit(RUNS_WITH_EIGHT)
    .collect(Collectors.toList());
  static List<Integer> fours = Stream.generate(() -> 4)
    .limit(RUNS - RUNS_WITH_TRANSITION - RUNS_WITH_EIGHT)
    .collect(Collectors.toList());
  static boolean flagErroneousRun = false;
  static boolean flagInitFailed = false;
  static boolean flagErroneousTransition = false;
  static boolean flagOnlyRookWasCreated = false;
  static boolean bishopFirstInitialized = false;
  static List<Task1Trace> traces = new ArrayList<>();
  static List<Task1Trace> defaultSizeTraces = new ArrayList<>();
  static List<Task1Trace> allTraces = new ArrayList<>();
  // For Init
  static int[] counterX = new int[4];
  static int[] counterY = new int[4];
  static int[] counterDir = new int[4];
  // For Rook
  static int[] turnCanMove = new int[4];
  static int[] turnCanNotMove = new int[4];

  private static boolean isInitTest = false;

  static void initTest() {
    if (isInitTest) {
      return;
    }
    isInitTest = true;
    ThreadLocalRandomTester.initialize();
    System.setOut(new PrintStream(outContent));
    columns.addAll(eights);
    rows.addAll(eights);
    columns.addAll(fours);
    rows.addAll(fours);
    var preTraces = new ArrayList<Task1Trace>();
    for (var i = 0; i < 5; i++) {
      var environment = new RookAndBishop(8, 8, 0, false);
      try {
      environment.execute();
      var robots = getRobots(World.getGlobalWorld());
      if (robots.size() == 0) {
        preTraces.add(new Task1Trace(null, columns.get(i), rows.get(i)));
      }
      var trace = fillTrace(robots, columns.get(i), rows.get(i));
      preTraces.add(trace);
      } catch (Exception ignored) {
      }
    }
    if (!preTraces.isEmpty() && preTraces.stream().anyMatch(trace -> trace.bishop.getTransitions().size() > 40) &&
      preTraces.stream().map(trace -> trace.bishop.getTransitions().size()).reduce(0, Integer::sum)
      < preTraces.stream().map(trace -> trace.rook.getTransitions().size()).reduce(0, Integer::sum)) {
      bishopFirstInitialized = true;
    }
    traces = new ArrayList<>();
    for (var i = 0; i < RUNS; i++) {
      var environment = new RookAndBishop(rows.get(i), columns.get(i), 0, false);
      try {
        environment.execute();
        var robots = getRobots(World.getGlobalWorld());
        if (robots.size() == 0) {
          traces.add(new Task1Trace(null, columns.get(i), rows.get(i)));
        }
        if (robots.size() == 1) {
          flagOnlyRookWasCreated = true;
        }
        var trace = fillTrace(robots, columns.get(i), rows.get(i));
        traces.add(trace);
      } catch (Exception e) {
        var trace = fillTrace(getRobots(World.getGlobalWorld()), columns.get(i), rows.get(i));
        trace.e = e;
        traces.add(new Task1Trace(e, columns.get(i), rows.get(i)));
      }
    }
    var workingTraces = traces.stream().filter(trace -> trace.e == null).collect(Collectors.toList());
    if (workingTraces.size() < 0.2 * RUNS) {
      flagErroneousRun = true;
    }
    allTraces = traces;
    traces = workingTraces;
    defaultSizeTraces = traces.stream().filter(trace -> trace.height == 8 && trace.width == 8).collect(Collectors.toList());
    analyseForInit();
    analyseForRook();
  }

  private static void analyseForInit() {
    for (var trace : traces.stream().filter(trace -> trace.height == 4 && trace.width == 4).collect(Collectors.toList())) {
      counterX[trace.bishopInitial().getX()]++;
      counterY[trace.bishopInitial().getY()]++;
      counterX[trace.rookInitial().getX()]++;
      counterY[trace.rookInitial().getY()]++;
    }
    for (var trace : traces) {
      counterDir[trace.rookInitial().getDirection().ordinal()]++;
      counterDir[trace.bishopInitial().getDirection().ordinal()]++;
    }
  }

  private static void analyseForRook() {
    for (Task1Trace trace : defaultSizeTraces) {
      int col = trace.width;
      int row = trace.height;
      Robot cur = trace.rookInitial();

      ROOK_STATE state = ROOK_STATE.DROP_COIN;
      var canMove = true;
      var turnCounter = 0;
      List<Transition> rook = trace.rook.getTransitions();
      for (int j = 0; j < rook.size(); j++) {
        Transition t = rook.get(j);
        if (state == ROOK_STATE.TURN) {
          if (t.action == Transition.RobotAction.TURN_LEFT) {
            turnCounter++;
            cur = t.robot;
            continue;
          } else {
            if (turnCounter >= 4) {
              turnCounter %= 4;
            }
            if (canMove) {
              turnCanMove[turnCounter]++;
            } else {
              turnCanNotMove[turnCounter]++;
            }
            turnCounter = 0;
            state = ROOK_STATE.DROP_COIN;
          }
        }
        if (state == ROOK_STATE.DROP_COIN) {
          state = ROOK_STATE.MOVE;
        } else {
          canMove = canMove(cur, col, row);
          if (!canMove) {
            j--;
          }
          state = ROOK_STATE.TURN;
        }
        cur = t.robot;
      }
    }
  }

  private static Task1Trace fillTrace(List<RobotTrace> robots, int width, int height) {
    var sign = bishopFirstInitialized ? -1 : 1;
    ToIntFunction<RobotTrace> toId = t -> sign * Integer.parseInt(t.getTransitions().get(0).robot.getId());
    var rook = robots.stream().min(Comparator.comparingInt(toId)).orElse(null);
    var bishop = robots.stream().max(Comparator.comparingInt(toId)).orElse(null);
    return new Task1Trace(rook, bishop, width, height);
  }


  @AfterAll
  static void restoreStreams() {
    System.setOut(originalOut);
  }

  static List<RobotTrace> getRobots(KarelWorld karelWorld) {
    return karelWorld.getTraces();
  }

  private static boolean canMove(Robot robot, int width, int height) {
    switch (robot.getDirection()) {
      case UP:
        return robot.getY() != height - 1;
      case RIGHT:
        return robot.getX() != width - 1;
      case DOWN:
        return robot.getY() != 0;
      case LEFT:
        return robot.getX() != 0;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Test
  @DisplayName("HX_LOW | Exceptions_During_Run")
  public void HX_LOW() {
    initTest();
    var workingRuns = allTraces.stream().filter(trace -> trace.e == null).count();
    assertTrue(workingRuns >= RUNS_WITH_EIGHT, "Only " + workingRuns + "/100 ran successfully. Remember your code has to run with any World Size.");
  }

  @Test
  @DisplayName("HX_HIGH | Exceptions_During_Run")
  public void HX_HIGH() {
    initTest();
    for (var trace : allTraces) {
      assertNull(trace.e, new RuntimeException(String.format("At least one of the test runs failed. This run had this World Size:" +
        " {width/NUMBER_OF_COLUMNS=%d, height/NUMBER_OF_ROWS=%d}", trace.width, trace.height), trace.e).toString());
    }
  }

  @Test
  @DisplayName("H1_T1 | Init_Muenzen")
  public void H1_T1() {
    initTest();
    for (var trace : traces) {
      assertEquals(0, trace.bishopInitial().getNumberOfCoins(), "Bishop does not have 0 coins.");
      assertTrue(trace.rookInitial().getNumberOfCoins() >= 12, "Rook has less than 12 coins");
      assertTrue(trace.rookInitial().getNumberOfCoins() <= 20, "Rook has more than 20 coins");
    }
  }

  @Test
  @DisplayName("H1_T2 | Init_Positionen")
  public void H1_T2() {
    initTest();
    var sz = traces.stream().filter(trace -> trace.height == 4 && trace.width == 4).count();
    String[] classesSpawn = new String[]{"robot-creations at coordinate 0", "robot-creations at coordinate 1",
      "robot-creations at coordinate 2", "robot-creations at coordinate 3"};
    var divider = 2.0;
    var expectedSpawn = new double[]{sz / divider, sz / divider, sz / divider, sz / divider};
    checkDistributionInit(counterX, expectedSpawn, classesSpawn, "initial robot-creation");
    checkDistributionInit(counterY, expectedSpawn, classesSpawn, "initial robot-creation");
  }

  @Test
  public void H1_T3() {
    initTest();
    var sz = traces.size();
    var divider = 2.0;
    var expectedSpawn = new double[]{sz / divider, sz / divider, sz / divider, sz / divider};
    String[] classesDirection = Arrays.stream(Direction.values())
      .map(direction -> "robot-creations with direction " + direction.toString())
      .toArray(String[]::new);
    checkDistributionInit(counterDir, expectedSpawn, classesDirection, "initial directions");
  }

  private void flagFailure(boolean isRookTest) {
    if (flagErroneousRun) {
      fail("Most of our test runs did need succeed and thus this submission can not receive points. " +
        "Make sure your submission runs without Exceptions or Robots crashing for any World Size.");
    }
    if (flagErroneousTransition) {
      fail("We could not find a matching legal action for two adjacent robot states." +
        " This means possible illegal methods were called or extra robots were created. Please check out the code.");
    }
    if (flagInitFailed) {
      fail("Less then two actions (e.g. Robot-Spawning, Moving, Turning) were under taken, which is required for any task.");
    }
    if (flagOnlyRookWasCreated && !isRookTest) {
      fail("No two robots were created at the start of at least one run");
    }
  }

  @Test
  @DisplayName("H3_1_T1 | Rook_Moves")
  public void H3_1_T1() {
    initTest();
    for (Task1Trace trace : traces) {
      assertFalse(trace.rook.getTransitions().isEmpty(), "Rook did not do any actions.");
    }
  }

  @Test
  @DisplayName("H3_1_T2 | Rook_Drops_Coins_And_Moves")
  public void H3_1_T2() {
    initTest();
    flagFailure(true);
    if (defaultSizeTraces.size() < RUNS_WITH_EIGHT * 5 / 6) {
      fail("Not enough test runs were successful (even with the default 8x8 world size) to evaluate the robots behavior");
    }
    for (Task1Trace trace : defaultSizeTraces) {
      assertFalse(trace.rook.getTransitions().isEmpty(), "Rook did not do any actions.");
      int col = trace.width;
      int row = trace.height;
      Robot cur = trace.rookInitial();

      ROOK_STATE state = ROOK_STATE.DROP_COIN;
      var canMove = true;
      List<Transition> rook = trace.rook.getTransitions();
      for (int j = 0; j < rook.size() - 1; j++) {
        Transition t = rook.get(j);
        if (state == ROOK_STATE.TURN) {
          if (t.action == Transition.RobotAction.TURN_LEFT) {
            cur = rook.get(j + 1).robot;
            continue;
          } else {
            state = ROOK_STATE.DROP_COIN;
          }
        }
        if (state == ROOK_STATE.DROP_COIN) {
          assertEquals(Transition.RobotAction.PUT_COIN, t.action);
          state = ROOK_STATE.MOVE;
        } else {
          canMove = canMove(cur, col, row);
          if (canMove) {
            assertEquals(Transition.RobotAction.MOVE, t.action);
          } else {
            j--;
          }
          state = ROOK_STATE.TURN;
        }
        cur = rook.get(j + 1).robot;
      }
    }
  }

  @Test
  @DisplayName("H3_1_T3 | Rook_Turns_Correctly")
  public void H3_1_T3() {
    initTest();
    flagFailure(true);
    if (defaultSizeTraces.size() < RUNS_WITH_EIGHT * 5 / 6) {
      fail("Not enough test runs were successful (even with the default 8x8 world size) to evaluate the robots behavior");
    }
    String[] turns = new String[]{"moves without turns", "left-turns", "turns of 180° degrees", "right-turns"};
    String moveFreely = "move freely";
    String cantMoveFreely = "not move, because it had a wall in front of it";
    var sumCanMove = (double) Arrays.stream(turnCanMove).reduce(Integer::sum).getAsInt();
    assertNotEquals(0, sumCanMove, "No turns when Rook could move were recorded. Maybe no runs were successful.");
    var expectedCanMove = new double[]{sumCanMove / 2, sumCanMove / 4, 0, sumCanMove / 4};
    checkDistributionRook(turnCanMove, expectedCanMove, turns, moveFreely);
    var sumCanNotMove = (double) Arrays.stream(turnCanNotMove).reduce(Integer::sum).getAsInt();
    assertNotEquals(0, sumCanNotMove, "No turns when Rook could not move were recorded. Maybe no runs were successful.");
    var expectedCanNotMove = new double[]{0, sumCanNotMove / 4, sumCanNotMove / 2, sumCanNotMove / 4};
    checkDistributionRook(turnCanNotMove, expectedCanNotMove, turns, cantMoveFreely);
  }

  private void checkDistributionInit(int[] actual, double[] expected, String[] classes, String distribution) {
    String elementTemplate = "There were unusually %s %s in a 4x4 World; Expected: %f Got: %d";
    String chiTestTemplate = "The distribution of " + distribution + " did not match well in a 4x4 World. Expected: %s Got: %s";
    checkDistribution(actual, expected, classes, elementTemplate, chiTestTemplate);
  }

  private void checkDistribution(int[] actual, double[] expected, String[] classes, String elementTemplate, String chiTestTemplate) {
    var sumChi = 0;
    for (int i = 0; i < 4; i++) {
      var chi = (expected[i] - actual[i]) * (expected[i] - actual[i]) / (expected[i] + 0.001);
      var amount = expected[i] < actual[i] ? "many" : "few";
      assertTrue(chi < chi10percent3df, String.format(elementTemplate, amount, classes[i], expected[i], actual[i]));
      sumChi += chi;
    }
    assertTrue(sumChi < chi2percent3df, String.format(chiTestTemplate, Arrays.toString(expected), Arrays.toString(actual)));
  }

  private void checkDistributionRook(int[] actual, double[] expected, String[] classes, String typ) {
    String elementTemplate = "There were unusually %s %s for the rook, when it could " + typ + "; Expected: %f Got: %d";
    String chiTestTemplate = "The distribution of turns for the rook (when it could " + typ + ") did not match well. Expected: %s Got: %s";
    checkDistribution(actual, expected, classes, elementTemplate, chiTestTemplate);
  }

  @Test
  @DisplayName("H3_2_T1 | Bishop_Moves")
  public void H3_2_T1() {
    initTest();
    for (Task1Trace trace : traces) {
      var consecutiveRook = 0;
      var curBishopIndex = 0;
      var transitions = trace.bishop.getTransitions();
      assertFalse(transitions.isEmpty(), "Bishop did not do any moves.");
      for (var t : trace.rook) {
        if (t.step > transitions.get(curBishopIndex).step && curBishopIndex != transitions.size() - 1) {
          assertTrue(consecutiveRook < 7, "Rook did too many consecutive moves, without any action from Bishop.");
          consecutiveRook = 0;
          while (curBishopIndex < transitions.size() - 1 && t.step > transitions.get(curBishopIndex).step) {
            curBishopIndex++;
          }
        } else {
          consecutiveRook++;
        }
      }
      assertTrue(consecutiveRook < 7, "Rook did too many consecutive moves, without any action from Bishop.");
    }
  }

  @Test
  @DisplayName("H3_2_T2_1 | Bishop_Moves_Correctly")
  public void H3_2_T2_1() {
    initTest();
    flagFailure(false);
    bishopMovesCorrectly(false);
  }

  @Test
  @DisplayName("H3_2_T2_1 | Bishop_Moves_Correctly")
  public void H3_2_T2_2() {
    initTest();
    flagFailure(false);
    bishopMovesCorrectly(true);
  }

  /**
   * Verifies correct function of bishop (excluding interactions with coins)
   * @param inverted if true assumes move -> turn right -> move -> turn left instead of the detailed description
   */
  private void bishopMovesCorrectly(boolean inverted) {
    var rightTurn = inverted ? 1 : 3;
    var leftTurn = inverted ? 3 : 1;
    if (defaultSizeTraces.size() < RUNS_WITH_EIGHT * 5 / 6) {
      fail("Not enough test runs were successful (even with the default 8x8 world size) to evaluate the robots behavior");
    }
    for (Task1Trace trace : defaultSizeTraces) {
      int col = trace.width;
      int row = trace.height;
      Robot cur = trace.bishopInitial();
      var state = canMove(cur, col, row) ? Transition.RobotAction.MOVE : Transition.RobotAction.TURN_LEFT;
      Transition.RobotAction priorState = null;
      var isSecondStep = false;
      var expectedTurns = 0;
      List<Transition> bishop = trace.bishop.getTransitions();
      for (int j = 0; j < bishop.size() - 1; j++) {
        Transition t = bishop.get(j);
        if (t.action == Transition.RobotAction.PICK_COIN) {
          if (isSecondStep) {
            fail("Bishop should only pick up coins after each iteration of the bishop loop, not after the first step.");
          } else {
            continue;
          }
        }
        if (t.action == Transition.RobotAction.PUT_COIN) {
          continue;
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && expectedTurns != 0) {
          fail("Expected more turns for bishop");
        }
        if (t.action == Transition.RobotAction.TURN_LEFT && expectedTurns == 0) {
          if (canMove(cur, col, row)) {
            fail("Although Bishop could move after doing its expected turns, it kept on turning.");
          } else {
            expectedTurns = isSecondStep ? 2 : leftTurn;
            isSecondStep = false;
          }
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && priorState == Transition.RobotAction.TURN_LEFT) {
          state = Transition.RobotAction.MOVE;
        }
        priorState = state;
        if (state != t.action) {
          int k = 0;
        }
        assertEquals(state, t.action, "Different next action then expected for bishop: " + t);
        if (t.action == Transition.RobotAction.MOVE) {
          expectedTurns = isSecondStep ? rightTurn : !canMove(cur, col, row) ? rightTurn : leftTurn;
          isSecondStep = !isSecondStep;
          state = Transition.RobotAction.TURN_LEFT;
        } else {
          expectedTurns--;
        }
        cur = bishop.get(j + 1).robot;
      }
    }
  }

  @Test
  @DisplayName("H3_2_T3_1 | Bishop_Acts_Correctly")
  public void H3_2_T3_1() {
    initTest();
    flagFailure(false);
    bishopActsCorrectly(false);
  }
  @Test
  @DisplayName("H3_2_T3_2 | Bishop_Acts_Correctly")
  public void H3_2_T3_2() {
    initTest();
    flagFailure(false);
    bishopActsCorrectly(true);
  }


  /**
   * Verifies correct function of bishop (including interactions with coins)
   * @param inverted if true assumes move -> turn right -> move -> turn left instead of the detailed description
   */
  private void bishopActsCorrectly(boolean inverted) {
    var rightTurn = inverted ? 1 : 3;
    var leftTurn = inverted ? 3 : 1;
    for (Task1Trace trace : traces) {
      int col = trace.width;
      int row = trace.height;
      var consecutiveRook = 0;
      var curBishopIndex = 0;
      List<Transition> transitions = trace.bishop.getTransitions();
      assertFalse(transitions.isEmpty(), "Bishop did not do any moves.");
      for (var t : trace.rook) {
        if (t.step > transitions.get(curBishopIndex).step && curBishopIndex != transitions.size() - 1) {
          assertTrue(consecutiveRook < 7, "Rook did too many consecutive moves, without any action from Bishop.");
          consecutiveRook = 0;
          while (curBishopIndex < transitions.size() - 1 && t.step > transitions.get(curBishopIndex).step) {
            curBishopIndex++;
          }
        } else {
          consecutiveRook++;
        }
      }
      assertTrue(consecutiveRook < 7, "Rook did too many consecutive moves, without any action from Bishop.");

      int[][] coins = new int[col][row];
      Robot cur = trace.bishopInitial();
      Transition.RobotAction state = canMove(cur, col, row) ? Transition.RobotAction.MOVE : Transition.RobotAction.TURN_LEFT;
      Transition.RobotAction priorState = null;
      var isSecondStep = false;
      var expectedTurns = 0;
      List<Transition> dropCoins = trace.rook.getTransitions().stream()
        .filter(t -> t.action == Transition.RobotAction.PUT_COIN).collect(Collectors.toList());
      var dropCoinsPointer = 0;
      for (int j = 0; j < transitions.size() - 1; j++) {
        Transition t = transitions.get(j);
        while (dropCoinsPointer < dropCoins.size() && t.step > dropCoins.get(dropCoinsPointer).step) {
          coins[dropCoins.get(dropCoinsPointer).robot.getX()][dropCoins.get(dropCoinsPointer).robot.getY()]++;
          dropCoinsPointer++;
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && expectedTurns != 0) {
          fail("Expected more turns for bishop");
        }
        if (t.action == Transition.RobotAction.TURN_LEFT && expectedTurns == 0) {
          if (canMove(cur, col, row)) {
            fail("Although Bishop could move after doing its expected turns, it kept on turning.");
          } else {
            expectedTurns = isSecondStep ? 2 : leftTurn;
            isSecondStep = false;
          }
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && priorState == Transition.RobotAction.TURN_LEFT) {
          state = coins[cur.getX()][cur.getY()] > 0 && !isSecondStep ? Transition.RobotAction.PICK_COIN : Transition.RobotAction.MOVE;
        }
        priorState = state;
        if (j == 0 || transitions.get(j - 1).step == t.step - 1 || coins[cur.getX()][cur.getY()] == 0) {
          assertEquals(state, t.action, "Different next action then expected for bishop: " + t);
        }
        if (t.action == Transition.RobotAction.MOVE) {
          expectedTurns = isSecondStep ? rightTurn : !canMove(cur, col, row) ? rightTurn : leftTurn;
          isSecondStep = !isSecondStep;
          state = Transition.RobotAction.TURN_LEFT;
        } else if (t.action == Transition.RobotAction.TURN_LEFT) {
          expectedTurns--;
        } else {
          state = canMove(cur, col, row) ? Transition.RobotAction.MOVE : Transition.RobotAction.TURN_LEFT;
          assertEquals(cur.getNumberOfCoins() + 1, transitions.get(j + 1).robot.getNumberOfCoins(),
            "Bishop did not increase his number of coins, when trying to pick up a coin.");
          coins[cur.getX()][cur.getY()]--;
        }
        cur = transitions.get(j + 1).robot;
      }
    }
  }

  @Test
  @DisplayName("H3_3_T1 | End_Upon_Meeting")
  public void H3_3_T1() {
    initTest();
    flagFailure(false);
    for (var trace : traces) {
      List<Transition> rookTransitions = trace.rook.getTransitions();
      for (int i = 0; i < rookTransitions.size() - 2; i++) {
        Transition trans = rookTransitions.get(i);
        var postRook = rookTransitions.get(i + 1);
        var rr = postRook.robot;
        var postBishops = trace.bishop.getTransitions().stream()
          .filter(t -> t.step > trans.step && t.step < postRook.step)
          .collect(Collectors.toList());
        if (postBishops.isEmpty()) {
          continue;
        }
        var relevantMove = false;
        for (int j = 0; j < postBishops.size() - 1; j++) {
          var pbBase = postBishops.get(j);
          var pb = postBishops.get(j + 1);
          if (pbBase.action == Transition.RobotAction.MOVE) {
            if (relevantMove) {
              if (pb.robot.getX() == rr.getX() && pb.robot.getY() == rr.getY() &&
                pb.action != Transition.RobotAction.NONE) {
                fail("The execution did not terminate, when rook and bishop first met.");
              }
            }
            relevantMove = !relevantMove;
          }
        }
        var pb = postBishops.get(postBishops.size() - 1);
        if (pb.robot.getX() == rr.getX() && pb.robot.getY() == rr.getY() &&
          pb.action != Transition.RobotAction.NONE) {
          fail("The execution did not terminate, when rook and bishop first met.");
        }
      }
    }
  }

  @Test
  @DisplayName("H3_3_T2 | End_Upon_Dropping_All_Coins")
  public void H3_3_T2() {
    initTest();
    flagFailure(false);
    List<Task1Trace> transitionTraces = traces.subList(0, RUNS_WITH_TRANSITION);
    for (var trace : transitionTraces) {
      List<Transition> rook = trace.rook.getTransitions().stream().filter(t -> t.action == Transition.RobotAction.MOVE).collect(Collectors.toList());
      for (int i = 0; i < rook.size() - 2; i++) {
        Transition t = rook.get(i);
        assertTrue(t.robot.hasAnyCoins(), "Rook kept on moving even though it dropped all its coins.");
      }
    }
  }

  @Test
  @DisplayName("H3_3_T3 | End_Messages")
  public void H3_3_T3() {
    initTest();
    flagFailure(false);
    List<Task1Trace> transitionTraces = traces.subList(0, RUNS_WITH_TRANSITION);
    String out = outContent.toString();
    System.setOut(originalOut);
    var results = Arrays.stream(out.split("gewo")).filter(s -> s.contains(ROOK_WIN) || s.contains(BISHOP_WIN))
      .collect(Collectors.toList());
    assertEquals(traces.size(), results.size(), "Some runs did not terminate with a proper log message like System.out.println(\"Der Turm hat gewonnen!\")");
    var rookLast = transitionTraces.stream().map(t -> t.rook.getTransitions().get(t.rook.getTransitions().size() - 1)).collect(Collectors.toList());
    for (int i = 0; i < rookLast.size(); i++) {
      Transition t = rookLast.get(i);
      var rookWin = t.robot.getNumberOfCoins() == 0;
      var expectedLog = rookWin ? ROOK_WIN : BISHOP_WIN;
      var unexpectedLog = rookWin ? BISHOP_WIN : ROOK_WIN;
      assertTrue(results.get(i).contains(expectedLog) && !results.get(i).contains(unexpectedLog),
        String.format("Output Message [%s] did not contain the expected output [%s]", results.get(i), expectedLog));
    }
  }

  public enum ROOK_STATE {
    DROP_COIN,
    MOVE,
    TURN
  }
}
