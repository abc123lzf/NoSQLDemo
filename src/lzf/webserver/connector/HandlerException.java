package lzf.webserver.connector;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 上午11:00:09
* @Description 类说明
*/
public class HandlerException extends Exception {

	private static final long serialVersionUID = 1565984040498374416L;
	
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
