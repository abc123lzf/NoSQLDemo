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

	public void setName(String name);
	
	public String getName();
	
	public Server getServer();
	
	public void setConnector(Connector connector);
	
	public List<Connector> getConnectors();
	
	public void setEngine();
}
