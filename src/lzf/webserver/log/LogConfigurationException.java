package lzf.webserver.log;

/**
 * @author Craig R. McClanahan
 */
public class LogConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LogConfigurationException() {
		super();
	}

	public LogConfigurationException(String message) {
		super(message);
	}

	public LogConfigurationException(Throwable cause) {
		super(cause);
	}

	public LogConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
