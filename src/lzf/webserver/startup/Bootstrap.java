package lzf.webserver.startup;

import lzf.webserver.LifecycleException;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;
import lzf.webserver.connector.NettyHandler;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��8�� ����3:28:20
* @Description ������
*/
public final class Bootstrap {
	
	public void init() {
		Server server = new StandardServer();
		try {
			Service service = new StandardService();
			service.setName("catalina");
			service.setServer(server);
			
			Connector connector = new Connector();
			NettyHandler handler = new NettyHandler();
			handler.setConnector(connector);
			
			service.addConnector(connector);
			server.addService(service);
			
			System.out.println(1);
			
			server.init();
			server.start();
			
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
	
	Bootstrap() {
	}
	
	public static void main(String[] args) {
		System.out.println(1);
		new Bootstrap().init();
	}
}
