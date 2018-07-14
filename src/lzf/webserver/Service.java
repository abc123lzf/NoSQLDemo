package lzf.webserver;

import java.util.List;

import lzf.webserver.connector.Connector;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����11:42:05
* @Description ������࣬������Server
* Service������Ӧ�����������͵������һ��Engine��������������
*/
public interface Service extends Lifecycle {

	/**
	 * ����Service�������
	 * @param name �����
	 */
	public void setName(String name) throws LifecycleException;
	
	/**
	 * ��ø�Service���������
	 */
	public String getName();
	
	/**
	 * ���ø����
	 * @param server Serverʵ��
	 */
	public void setServer(Server server) throws LifecycleException;
	
	/**
	 * ��ø������Server
	 */
	public Server getServer();
	
	/**
	 * ���������ʵ��
	 * @param connector ������ʵ��
	 */
	public void addConnector(Connector connector) throws LifecycleException;
	
	/**
	 * ���Service������е�������
	 * @return ����������������List
	 */
	public List<Connector> getConnectors();
	
	/**
	 * ����ȫ����������
	 * @param engine ����ʵ��
	 * @throws LifecycleException 
	 */
	public void setEngine(Engine engine) throws LifecycleException;
	
	/**
	 * ���ȫ����������
	 */
	public Engine getEngine();
}
