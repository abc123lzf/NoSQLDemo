package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.Container;
import lzf.webserver.Valve;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����2:52:27
* @Description ���ų����࣬����ʵ����Ӧ�̳иó�����
*/
public abstract class ValveBase implements Valve {
	
	protected final Log log = LogFactory.getLog(ValveBase.class); 
	
	protected Valve next = null;
	
	protected Container container = null;
	
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
	 * ��ȡ�÷�������������ʵ��
	 * @return Container����ʵ��
	 */
	@Override
	public Container getContainer() {
		return container;
	}
	
	/**
	 * ���ø÷�������������ʵ��
	 * @param container Container����ʵ��
	 */
	@Override
	public void setContainer(Container container) {
		this.container = container;
	}
		
	/**
	 * ���ž����ҵ���߼�ʵ��
	 * @param request �������
	 * @param response ��Ӧ����
	 */
	@Override
	public abstract void invoke(Request request, Response response)
			throws IOException, ServletException;
	

	/**
	 * ��������ܵ�������������������
	 * @return ����lzf.webserver.valve[containerName]
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder(this.getClass().getName());
		sb.append('[');
		
		if(getContainer() == null) {
			sb.append("Container is null");
		} else {
			sb.append(getContainer().getName());
		}
		
		sb.append(']');
		return sb.toString();
	}
}
