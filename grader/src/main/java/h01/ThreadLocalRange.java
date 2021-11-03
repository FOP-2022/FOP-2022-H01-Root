package h01;

public class ThreadLocalRange {
  int low;
  int up;
  int maxRepetition;
  int currentNumber;
  int currentRepetition;

  public ThreadLocalRange(int low, int up) {
    this(low, up, 3);
  }

  public ThreadLocalRange(int low, int up, int maxRepetition) {
    if (maxRepetition < 0) {
      throw new IllegalArgumentException("ThreadLocalRange could not be constructed with negative maxRepetition");
    }
    this.low = low;
    this.up = up;
    this.maxRepetition = maxRepetition;
    this.currentNumber = low;
    this.currentRepetition = 0;
  }

  public ThreadLocalRange(Range range) {
    this(range.low, range.up);
  }

  public int next() {
    if (this.up <= this.low) {
      throw new IllegalArgumentException("ThreadLocalRandom.current().nextInt-Aufruf mit " +
        "oberer Schranke <= unterer Schranke");
    }
    var result = currentNumber;
    currentRepetition += 1;
    if (currentRepetition == maxRepetition) {
      currentNumber += 1;
      currentRepetition = 0;
      if (currentNumber == up) {
        currentNumber = low;
      }
    }
    return result;
  }
}
