package h01;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadLocalRange {
  private List<Integer> sequence;
  int low;
  int up;
  int maxRepetition; // unused
  int currentNumber;
  int currentRepetition;

  public ThreadLocalRange(int low, int up) {
    this(low, up, 1);
  }

  public ThreadLocalRange(int low, int up, int maxRepetition) {
    if (maxRepetition < 0) {
      throw new IllegalArgumentException("ThreadLocalRange could not be constructed with negative maxRepetition");
    }
    this.low = low;
    this.up = up;
    this.currentNumber = 0;
    this.currentRepetition = 0;
    this.sequence = IntStream.range(low, up).boxed().collect(Collectors.toList());
    Collections.shuffle(this.sequence);
  }

  public ThreadLocalRange(Range range) {
    this(range.low, range.up);
  }

  public int next() {
    var result = sequence.get(currentNumber++);
    if (currentNumber == sequence.size()) {
      this.currentNumber = 0;
      this.sequence = IntStream.range(low, up).boxed().collect(Collectors.toList());
      Collections.shuffle(this.sequence);
    }
    return result;
  }
}


