package lzf.webserver.log;

/**
 * <p>
 * An exception that is thrown only if a suitable <code>LogFactory</code> or
 * <code>Log</code> instance cannot be created by the corresponding factory
 * methods.
 * </p>
 *
 * @author Craig R. McClanahan
 */
public class LogConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new exception with <code>null</code> as its detail message.
	 */
	public LogConfigurationException() {
		super();
	}

	/**
	 * Construct a new exception with the specified detail message.
	 *
	 * @param message
	 *            The detail message
	 */
	public LogConfigurationException(String message) {
		super(message);
	}

	/**
	 * Construct a new exception with the specified cause and a derived detail
	 * message.
	 *
	 * @param cause
	 *            The underlying cause
	 */
	public LogConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new exception with the specified detail message and cause.
	 *
	 * @param message
	 *            The detail message
	 * @param cause
	 *            The underlying cause
	 */
	public LogConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
