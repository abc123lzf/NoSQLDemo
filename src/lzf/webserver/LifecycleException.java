package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��10�� ����3:58:41
* @Description �쳣��
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
