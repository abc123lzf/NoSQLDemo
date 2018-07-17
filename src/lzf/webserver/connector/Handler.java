package lzf.webserver.connector;

import java.util.concurrent.Executor;

import lzf.webserver.Lifecycle;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����7:29:38
* @Description ���ӽ������ӿڣ����ڽ��տͻ�������
*/
public interface Handler extends Lifecycle {
	
	/**
	 * ���øý�����������������
	 * @param connector ������ʵ��
	 */
	public void setConnector(Connector connector);
	
	/**
	 * ���ظý�����������������
	 * @return ������ʵ��
	 */
	public Connector getConnector();
	
	/**
	 * ���ظý������������̳߳�
	 * @return Executor�̳߳�
	 */
	public Executor getExecutor();
	
}
