package h01;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTester {
  private static final InheritableThreadLocal<ThreadLocalRandomTester> factory = new InheritableThreadLocal<>();

  private final HashMap<Range, ThreadLocalRange> usedRanges = new HashMap<>();

  public static void initialize() {
    factory.set(new ThreadLocalRandomTester());
  }

  /**
   * Replaces {@link ThreadLocalRandom#current()}
   */
  public static ThreadLocalRandomTester current() {
    return factory.get();
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt(int, int)}
   */
  public int nextInt(int a, int b) {
    return usedRanges.computeIfAbsent(new Range(a, b), ThreadLocalRange::new).next();
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt(int)}
   */
  public int nextInt(int a) {
    return nextInt(0, a);
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt(int)}
   */
  public boolean nextBoolean() {
    return nextInt(0, 2) != 0;
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextInt()}
   */
  public int nextInt() {
    return nextInt(0, 200);
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextFloat()}
   */
  public float nextFloat() {
    return ThreadLocalRandom.current().nextFloat();
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextDouble()}
   */
  public double nextDouble() {
    return ThreadLocalRandom.current().nextDouble();
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextDouble(double, double)}
   */
  public double nextDouble(double a, double b) {
    return ThreadLocalRandom.current().nextDouble(a, b);
  }

  /**
   * Replaces {@link ThreadLocalRandom#nextDouble(double)}
   */
  public double nextDouble(double a) {
    return ThreadLocalRandom.current().nextDouble(a);
  }
}
