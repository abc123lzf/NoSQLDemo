package lzf.webserver.log;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��11�� ����6:25:04
* @Description �ͻ��˷�����־��¼�ӿڣ�һ������Valve���
*/
public interface AccessLog {

	public void log(ServletRequest request, ServletResponse response);
	
}
