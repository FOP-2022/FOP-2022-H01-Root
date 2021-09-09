package h01.misc;

public class PropertyException extends RuntimeException {
	public PropertyException(String message, Exception e) {
		super(message, e);
	}
	
	public PropertyException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
