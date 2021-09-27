package H01;

import fopbot.Robot;
import fopbot.RobotTrace;


public class Task1Trace {

  RobotTrace rook;
  RobotTrace bishop;

  public Task1Trace(RobotTrace rook, RobotTrace bishop) {
    this.rook = rook;
    this.bishop = bishop;
  }

  public Robot bishopInitial() {
    return bishop.getTransitions().get(0).robot;
  }

  public Robot rookInitial() {
    return rook.getTransitions().get(0).robot;
  }
}
