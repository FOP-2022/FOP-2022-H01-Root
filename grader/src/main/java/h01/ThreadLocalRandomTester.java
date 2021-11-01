package h01;

import org.sourcegrade.insnreplacer.ThreadLocalInstanceFactory;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTester {
  private static final ThreadLocalInstanceFactory<ThreadLocalRandomTester> factory = new ThreadLocalInstanceFactory<>();

  public static void initialize() {
    factory.setValue(new ThreadLocalRandomTester());
  }

  /**
   * Replaces {@link ThreadLocalRandom#current()}
   */
  public static ThreadLocalRandomTester current() {
    return factory.getValue();
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt(int, int)}
   */
  public int nextInt(int a, int b) {
    System.err.println("Getting next int between bounds " + a + " and " + b);
    return 0;
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt(int)}
   */
  public int nextInt(int a) {
    System.err.println("Getting next int until bound " + a);
    return 0;
  }
}
