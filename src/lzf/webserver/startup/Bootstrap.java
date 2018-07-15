package lzf.webserver.startup;

import lzf.webserver.LifecycleException;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��8�� ����3:28:20
* @Description ������
*/
public final class Bootstrap {
	
	public static void main(String[] args) {
		Server server = new StandardServer();
		try {
			Service service = new StandardService();
			
			service.setServer(server);
			service.addConnector(new Connector());
			
			server.setService(service);
			
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
}
