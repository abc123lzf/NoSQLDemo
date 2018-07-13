package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月10日 下午3:58:41
* @Description 异常类
*/
public class LifecycleException extends Exception {

	private static final long serialVersionUID = 1876009575922320781L;

	public LifecycleException() {
        super();
    }
	
	public LifecycleException(String message) {
        super(message);
    }
	
	public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public LifecycleException(Throwable cause) {
        super(cause);
    }
}
