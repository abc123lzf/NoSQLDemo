package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.Valve;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����2:52:27
* @Description ���ų����࣬����ʵ����Ӧ�̳иó�����
*/
public abstract class ValveBase implements Valve {
	
	protected Valve next;
	
	/**
	 * ��ȡ��һ���ܵ�
	 * @return Valve ��һ���ܵ�
	 */
	@Override
	public Valve getNext() {
		return next;
	}
		
	/**
	 * ������һ���ܵ�
	 * @param valve ��һ���ܵ�ʵ��
	 */
	@Override
	public void setNext(Valve valve) {
		this.next = valve;
	}
		
	/**
	 * ���ž����ҵ���߼�ʵ�֣������ڸ÷���������next.invoke()
	 * @param request �������
	 * @param response ��Ӧ����
	 */
	@Override
	public abstract void invoke(Request request, Response response)
			throws IOException, ServletException;
}
