package lzf.webserver.startup;

import lzf.webserver.LifecycleException;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月8日 下午3:28:20
* @Description 启动类
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
