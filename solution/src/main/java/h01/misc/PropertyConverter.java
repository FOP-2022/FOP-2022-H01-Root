package h01.misc;

@FunctionalInterface
public interface PropertyConverter<T> {
  T convert(String str) throws PropertyException;
}
