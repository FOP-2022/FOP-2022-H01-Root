package h01;

import fopbot.Robot;
import fopbot.RobotTrace;
import fopbot.Transition;

import java.util.stream.Stream;

public class Task1Trace {

  RobotTrace rook;
  RobotTrace bishop;
  int width;
  int height;
  Exception e;

  public Task1Trace(Exception e, int width, int height) {
    this.e = e;
    this.width = width;
    this.height = height;
  }

  public Task1Trace(RobotTrace rook, RobotTrace bishop, int width, int height) {
    this.rook = rook;
    this.bishop = bishop;
    this.width = width;
    this.height = height;
    if (Stream.concat(rook.getTransitions().stream(), bishop.getTransitions().stream())
      .anyMatch(transition -> transition.action == Transition.RobotAction.SET_X ||
        transition.action == Transition.RobotAction.SET_Y)) {
      this.e = new RuntimeException("This particular run made use of the banned methods set_x or set_y");
    }
  }

  public Robot bishopInitial() {
    return bishop.getTransitions().get(0).robot;
  }

  public Robot rookInitial() {
    return rook.getTransitions().get(0).robot;
  }
}
