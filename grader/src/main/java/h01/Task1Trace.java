package h01;

import fopbot.Robot;
import fopbot.RobotTrace;

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
  }

  public Robot bishopInitial() {
    return bishop.getTransitions().get(0).robot;
  }

  public Robot rookInitial() {
    return rook.getTransitions().get(0).robot;
  }
}
