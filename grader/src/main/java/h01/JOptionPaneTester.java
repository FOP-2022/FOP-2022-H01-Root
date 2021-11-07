package h01;

import org.sourcegrade.insnreplacer.ThreadLocalInstanceFactory;

import javax.swing.*;

import java.awt.*;

public class JOptionPaneTester {
  private static final ThreadLocalInstanceFactory<JOptionPaneTester> factory = new ThreadLocalInstanceFactory<>();

  public static void initialize() {
    factory.setValue(new JOptionPaneTester());
  }

  /**
   * Replaces {@link JOptionPane#showMessageDialog}
   */
  public static void showMessageDialog(Component parentComponent, Object message) {
    System.err.println("Using showMessageDialog!");
  }
}
