package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:39:45
* @Description 容器异常类，仅用于实现Container容器的类抛出异常
*/
public class ContainerException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ContainerException() {
        super();
    }
	
	public ContainerException(String message) {
        super(message);
    }
	
	public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public ContainerException(Throwable cause) {
        super(cause);
    }
}
