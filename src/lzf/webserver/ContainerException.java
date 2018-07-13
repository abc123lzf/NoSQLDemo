package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����11:39:45
* @Description �����쳣�࣬������ʵ��Container���������׳��쳣
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
