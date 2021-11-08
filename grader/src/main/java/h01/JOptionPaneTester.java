package h01;

import javax.swing.*;
import java.awt.*;

public class JOptionPaneTester {
  /**
   * Replaces {@link JOptionPane#showMessageDialog}
   */
  public static void showMessageDialog(Component parentComponent, Object message) {
    System.err.println("Using showMessageDialog!");
  }
}
