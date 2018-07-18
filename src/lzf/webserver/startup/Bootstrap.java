package lzf.webserver.startup;

import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;
import lzf.webserver.connector.NettyHandler;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��8�� ����3:28:20
* @Description ������
*/
public final class Bootstrap {
	
	private static final Log log = LogFactory.getLog(Bootstrap.class);
	
	public void init() {
		log.info("start");
		Server server = new StandardServer();
		try {
			Service service = new StandardService();
			service.setName("catalina");
			service.setServer(server);
			
			Connector connector = new Connector();
			NettyHandler handler = new NettyHandler(connector);
			handler.setConnector(connector);
			connector.setHandler(handler);
			
			service.addConnector(connector);
			server.addService(service);
			
			server.init();
			server.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Bootstrap() {
	}
	
	public static void main(String[] args) {
		new Bootstrap().init();
	}
}
