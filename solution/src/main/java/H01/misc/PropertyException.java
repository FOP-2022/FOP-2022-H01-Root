package H01.misc;

public class PropertyException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public PropertyException(String message, Exception e) {
    super(message, e);
  }

  public PropertyException(String message) {
    super(message);
  }
}
