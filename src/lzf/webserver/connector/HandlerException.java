package lzf.webserver.connector;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午8:33:36
* @Description 连接接收器异常类
*/
public class HandlerException extends Exception {

	private static final long serialVersionUID = 1L;

	public HandlerException() {
        super();
    }
	
	public HandlerException(String message) {
        super(message);
    }
	
	public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public HandlerException(Throwable cause) {
        super(cause);
    }
}
