package lzf.webserver;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:31:57
* @Description �ܵ�ģ���еķ���
*/
public interface Valve {

	/**
	 * ��ȡ��һ������
	 * @return ��һ������ʵ�����������򷵻�null
	 */
	public Valve getNext();
	
	/**
	 * ������һ������
	 * @param valve ��һ������ʵ��
	 */
	public void setNext(Valve valve);
	
	/**
	 * ����ִ�з���������ǻ������ţ���Ӧ�������������ܵ�����ĵ�һ������
	 * @param request ��������װ�õ�Request����
	 * @param response ��������װ�õ�Response����
	 */
	public void invoke(Request request, Response response) 
			throws IOException, ServletException;
}
