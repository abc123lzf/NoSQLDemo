package lzf.webserver.connector;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����8:33:36
* @Description ���ӽ������쳣��
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
