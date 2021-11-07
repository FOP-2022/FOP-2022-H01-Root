package h01;

import java.util.Objects;

public class Range {
  int low;
  int up;

  public Range(int a, int b) {
    this.low = a;
    this.up = b;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Range range = (Range) o;
    return low == range.low && up == range.up;
  }

  @Override
  public int hashCode() {
    return Objects.hash(low, up);
  }
}
