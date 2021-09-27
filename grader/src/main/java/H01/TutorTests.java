package H01;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import fopbot.*;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

/**
 * @author Thomas Rothenbächer
 */
@TestForSubmission("H01")
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
  static List<Integer> rows = new ArrayList<>(List.of(11, 10, 8, 1, 1, 13, 11, 7, 6, 10, 13, 11, 7, 10, 11, 1, 6));
  static List<Integer> fours = Stream.generate(() -> 4)
    .limit(RUNS - RUNS_WITH_TRANSITION)
    .collect(Collectors.toList());
  static boolean flagErroneousRun = false;
  static boolean flagInitFailed = false;
  static boolean flagErroneousTransition = false;
  static boolean flagOnlyRookWasCreated = false;
  static List<Task1Trace> traces = new ArrayList<>();
  // For Init
  static int[] counterX = new int[4];
  static int[] counterY = new int[4];
  static int[] counterDir = new int[4];
  // For Rook
  static int[] turnCanMove = new int[4];
  static int[] turnCanNotMove = new int[4];
  private static Exception environmentExecuteException;

  @BeforeAll
  static void initTest() {
    System.setOut(new PrintStream(outContent));
    columns.addAll(fours);
    rows.addAll(fours);
    traces = new ArrayList<>();
    for (var i = 0; i < RUNS; i++) {
      var environment = new Task1.RookAndBishop(rows.get(i), columns.get(i), 0, false);
      try {
        environment.execute();
      } catch (Exception e) {
        environmentExecuteException = e;
        flagErroneousRun = true;
      }
      var robots = getRobots(World.getGlobalWorld());
      if (robots.size() == 0) {
        flagInitFailed = true;
        return;
      }
      if (robots.size() == 1) {
        flagOnlyRookWasCreated = true;
      }
      var trace = fillTrace(robots);
      traces.add(trace);
    }
    analyseForInit();
    analyseForRook();
  }

  private static void analyseForInit() {
    for (var trace : traces.subList(RUNS_WITH_TRANSITION, RUNS)) {
      counterX[trace.bishopInitial().getX()]++;
      counterY[trace.bishopInitial().getY()]++;
      counterX[trace.rookInitial().getX()]++;
      counterY[trace.rookInitial().getY()]++;
      counterDir[trace.rookInitial().getDirection().ordinal()]++;
      counterDir[trace.bishopInitial().getDirection().ordinal()]++;
    }
  }

  private static void analyseForRook() {
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (int i = 0; i < subList.size(); i++) {
      Task1Trace trace = subList.get(i);
      int col = columns.get(i);
      int row = rows.get(i);
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

  private static Task1Trace fillTrace(List<RobotTrace> robots) {
    var rook = robots.stream().min(Comparator.comparingInt(t -> t.getTransitions().get(0).step)).orElse(null);
    var bishop = robots.stream().max(Comparator.comparingInt(t -> t.getTransitions().get(0).step)).orElse(null);
    return new Task1Trace(rook, bishop);
  }

  @AfterAll
  static void restoreStreams() {
    System.setOut(originalOut);
  }

  private static boolean rEquals(Robot r1, Robot r2) {
    if (r1 == null && r2 == null) return true;
    if (r1 == null ^ r2 == null) return false;
    return r1.getNumberOfCoins() == r2.getNumberOfCoins() &&
      r1.getX() == r2.getX() &&
      r1.getY() == r2.getY() &&
      r1.getDirection() == r2.getDirection() &&
      r1.getId().equals(r2.getId());
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
  @DisplayName("H1_T1 | Init_Muenzen")
  public void H1_T1() {
    flagFailure(false);
    for (var trace : traces.subList(RUNS_WITH_TRANSITION, RUNS)) {
      assertEquals(0, trace.bishopInitial().getNumberOfCoins(), "Bishop does not have 0 coins.");
      assertTrue(trace.rookInitial().getNumberOfCoins() >= 12, "Rook has less than 12 coins");
      assertTrue(trace.rookInitial().getNumberOfCoins() <= 20, "Rook has more than 20 coins");
    }
  }

  @Test
  @DisplayName("H1_T2 | Init_Positionen")
  public void H1_T2() {
    flagFailure(false);
    var sz = RUNS - RUNS_WITH_TRANSITION;
    String[] classesSpawn = new String[]{"robot-creations at coordinate 0", "robot-creations at coordinate 1",
      "robot-creations at coordinate 2", "robot-creations at coordinate 3"};
    var divider = 2.0;
    var expectedSpawn = new double[]{sz / divider, sz / divider, sz / divider, sz / divider};
    checkDistributionInit(counterX, expectedSpawn, classesSpawn, "initial robot-creation");
    checkDistributionInit(counterY, expectedSpawn, classesSpawn, "initial robot-creation");
  }

  @Test
  @DisplayName("H1_T3 | Init_Directions")
  public void H1_T3() {
    flagFailure(false);
    var sz = RUNS - RUNS_WITH_TRANSITION;
    var divider = 2.0;
    var expectedSpawn = new double[]{sz / divider, sz / divider, sz / divider, sz / divider};
    String[] classesDirection = Arrays.stream(Direction.values())
      .map(direction -> "robot-creations with direction" + direction.toString())
      .toArray(String[]::new);
    checkDistributionInit(counterDir, expectedSpawn, classesDirection, "initial directions");
  }

  private void flagFailure(boolean isRookTest) {
    if (flagErroneousRun) {
      fail("The student's code failed in a run.", environmentExecuteException);
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
    flagFailure(true);
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (Task1Trace trace : subList) {
      assertFalse(trace.rook.getTransitions().isEmpty(), "Rook did not do any actions.");
    }
  }

  @Test
  @DisplayName("H3_1_T2 | Rook_Drops_Coins_And_Moves")
  public void H3_1_T2() {
    flagFailure(true);
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (int i = 0; i < subList.size(); i++) {
      Task1Trace trace = subList.get(i);
      assertFalse(trace.rook.getTransitions().isEmpty(), "Rook did not do any actions.");
      int col = columns.get(i);
      int row = rows.get(i);
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
    flagFailure(true);
    String[] turns = new String[]{"moves without turns", "left-turns", "turns of 180� degrees", "right-turns"};
    String moveFreely = "move freely";
    String cantMoveFreely = "not move, because it had a wall in front of it";
    var sumCanMove = (double) Arrays.stream(turnCanMove).reduce(Integer::sum).getAsInt();
    var expectedCanMove = new double[]{sumCanMove / 2, sumCanMove / 4, 0, sumCanMove / 4};
    checkDistributionRook(turnCanMove, expectedCanMove, turns, moveFreely);
    var sumCanNotMove = (double) Arrays.stream(turnCanNotMove).reduce(Integer::sum).getAsInt();
    var expectedCanNotMove = new double[]{0, sumCanNotMove / 4, sumCanNotMove / 2, sumCanNotMove / 4};
    checkDistributionRook(turnCanNotMove, expectedCanNotMove, turns, cantMoveFreely);
  }


  private void checkDistributionInit(int[] actual, double[] expected, String[] classes, String distribution) {
    String elementTemplate = "There were unusually %s %s ; Expected: %f Got: %d";
    String chiTestTemplate = "The distribution of " + distribution + " did not match well. Expected: %s Got: %s";
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
    flagFailure(false);
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (int i = 0; i < subList.size(); i++) {
      Task1Trace trace = subList.get(i);
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
  @DisplayName("H3_2_T2 | Bishop_Moves_Correctly")
  public void H3_2_T2() {
    flagFailure(false);
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (int i = 0; i < subList.size(); i++) {
      Task1Trace trace = subList.get(i);
      int col = columns.get(i);
      int row = rows.get(i);
      Robot cur = trace.bishopInitial();
      var state = canMove(cur, col, row) ? Transition.RobotAction.MOVE : Transition.RobotAction.TURN_LEFT;
      Transition.RobotAction priorState = null;
      var isSecondStep = false;
      var expectedTurns = 0;
      List<Transition> bishop = trace.bishop.getTransitions();
      for (int j = 0; j < bishop.size() - 1; j++) {
        Transition t = bishop.get(j);
        if (t.action == Transition.RobotAction.PICK_COIN || t.action == Transition.RobotAction.PUT_COIN) {
          continue;
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && expectedTurns != 0) {
          fail("Expected more turns for bishop");
        }
        if (t.action == Transition.RobotAction.TURN_LEFT && expectedTurns == 0) {
          if (canMove(cur, col, row)) {
            fail("Although Bishop could move after doing its expected turns, it kept on turning.");
          } else {
            expectedTurns = isSecondStep ? 2 : 1;
            isSecondStep = false;
          }
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && priorState == Transition.RobotAction.TURN_LEFT) {
          state = Transition.RobotAction.MOVE;
        }
        priorState = state;
        assertEquals(state, t.action, "Different next action then expected for bishop: " + t.toString());
        if (t.action == Transition.RobotAction.MOVE) {
          expectedTurns = isSecondStep ? 3 : !canMove(cur, col, row) ? 3 : 1;
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
  @DisplayName("H3_2_T3 | Bishop_Acts_Correctly")
  public void H3_2_T3() {
    flagFailure(false);
    List<Task1Trace> subList = traces.subList(0, RUNS_WITH_TRANSITION);
    for (int i = 0; i < subList.size(); i++) {
      Task1Trace trace = subList.get(i);
      int col = columns.get(i);
      int row = rows.get(i);
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
            expectedTurns = isSecondStep ? 2 : 1;
            isSecondStep = false;
          }
        }
        if (t.action != Transition.RobotAction.TURN_LEFT && priorState == Transition.RobotAction.TURN_LEFT) {
          state = coins[cur.getX()][cur.getY()] > 0 && !isSecondStep ? Transition.RobotAction.PICK_COIN : Transition.RobotAction.MOVE;
        }
        priorState = state;
        assertEquals(state, t.action, "Different next action then expected for bishop: " + t.toString());
        if (t.action == Transition.RobotAction.MOVE) {
          expectedTurns = isSecondStep ? 3 : !canMove(cur, col, row) ? 3 : 1;
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
    flagFailure(false);
    List<Task1Trace> transitionTraces = traces.subList(0, RUNS_WITH_TRANSITION);
    for (var trace : transitionTraces) {
      List<Transition> rookTransitions = trace.rook.getTransitions();
      for (int i = 0; i < rookTransitions.size() - 1; i++) {
        Transition trans = rookTransitions.get(i);
        var postBishop = trace.bishop.getTransitions().stream().filter(t -> t.step > trans.step).min(Comparator.comparingInt(t -> t.step));
        var postRook = rookTransitions.get(i + 1);
        if (postBishop.isPresent()) {
          var pbr = postBishop.get().robot;
          var prr = postRook.robot;
          if (pbr.getX() == prr.getX() && pbr.getY() == prr.getY() && postBishop.get().action != Transition.RobotAction.NONE && postRook.step > postBishop.get().step) {
            fail("The execution did not terminate, when rook and bishop first met.");
          }
        }
      }
    }
  }

  @Test
  @DisplayName("H3_3_T2 | End_Upon_Dropping_All_Coins")
  public void H3_3_T2() {
    flagFailure(false);
    List<Task1Trace> transitionTraces = traces.subList(0, RUNS_WITH_TRANSITION);
    for (var trace : transitionTraces) {
      List<Transition> rook = trace.rook.getTransitions().stream().filter(t -> t.action == Transition.RobotAction.MOVE).collect(Collectors.toList());
      for (int i = 0; i < rook.size() - 2; i++) {
        Transition t = rook.get(i);
        assertTrue(t.action != Transition.RobotAction.MOVE || t.robot.hasAnyCoins(), "Rook kept on moving even though it dropped all its coins.");
      }
    }
  }

  @Test
  @DisplayName("H3_3_T3 | End_Messages")
  public void H3_3_T3() {
    flagFailure(false);
    List<Task1Trace> transitionTraces = traces.subList(0, RUNS_WITH_TRANSITION);
    String out = outContent.toString();
    try (PrintStream ps = new PrintStream(new FileOutputStream("filename.txt"))) {
      ps.print(out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    System.setOut(originalOut);
    var results = Arrays.stream(out.split("\n")).filter(s -> s.contains(ROOK_WIN) || s.contains(BISHOP_WIN))
      .collect(Collectors.toList());
    assertEquals(RUNS, results.size(), "Some runs did not terminate with a proper log message like System.out.println(\"Der Turm hat gewonnen!\")");
    var rookLast = transitionTraces.stream().map(t -> t.rook.getTransitions().get(t.rook.getTransitions().size() - 1)).collect(Collectors.toList());
    for (int i = 0; i < rookLast.size(); i++) {
      Transition t = rookLast.get(i);
      var rookWin = t.robot.getNumberOfCoins() == 0;
      var expectedLog = rookWin ? ROOK_WIN : BISHOP_WIN;
      assertTrue(results.get(i).contains(expectedLog),
        String.format("Output Message [%s] did not contain the expected output [%s]", results.get(i), expectedLog));
    }
  }

  public enum ROOK_STATE {
    DROP_COIN,
    MOVE,
    TURN
  }
}
