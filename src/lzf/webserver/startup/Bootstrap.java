package lzf.webserver.startup;

import java.io.File;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import lzf.webserver.connector.Connector;
import lzf.webserver.connector.NettyHandler;
import lzf.webserver.core.StandardContext;
import lzf.webserver.core.StandardEngine;
import lzf.webserver.core.StandardHost;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;
import lzf.webserver.core.StandardWrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.XMLUtil;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月8日 下午3:28:20
* @Description 启动类
*/
public final class Bootstrap {
	
	private static final Log log = LogFactory.getLog(Bootstrap.class);
	//主运行目录
	public static final File MAIN_PATH = new File(System.getProperty("user.dir"));
	//配置文件目录
	public static final File CONF_PATH = new File(MAIN_PATH.getPath() + File.separator + "conf");
	//日志文件目录
	public static final File LOG_PATH = new File(MAIN_PATH.getPath() + File.separator + "log");
	
	@SuppressWarnings("unused")
	private static Bootstrap bootstrap;
	
	public void boot() throws Exception {
		log.info("boot");
		
		//读取server.xml文件并生成相应的对象
		Element serverRoot;
		try {
			serverRoot = XMLUtil.getXMLRoot(new File(CONF_PATH.getPath() + File.separator + "server.xml"));
		} catch (DocumentException e) {
			log.error("server.xml error", e);
			return;
		}
		
		StandardServer server = new StandardServer();
		
		String shutdownPortStr = serverRoot.attributeValue("port");
		if(shutdownPortStr != null)
			server.setPort(Integer.valueOf(shutdownPortStr));
		String shutdownCmd = serverRoot.attributeValue("shutdown");
		if(shutdownCmd != null)
			server.setShutdownCommand(shutdownCmd);
		
		for(Element serviceRoot: serverRoot.elements("Service")) {
			
			StandardService service = new StandardService();
			
			String serviceName = serviceRoot.attributeValue("name");
			System.out.println(serviceName);
			if(serviceName != null)
				service.setName(serviceName);
			
			for(Element connectorRoot : serviceRoot.elements("Connector")) {
				
				Connector connector = new Connector(service);
				String connectorPort = connectorRoot.attributeValue("port");
				String connectionTimeout = connectorRoot.attributeValue("connectionTimeout");
				String handlerType = connectorRoot.attributeValue("handler");
				if(connectorPort != null)
					connector.setPort(Integer.valueOf(connectorPort));
				if(connectionTimeout != null)
					connector.setTimeOut(Integer.valueOf(connectionTimeout));
				
				if(handlerType != null) {
					if(handlerType.toLowerCase().equals("netty")) {
						NettyHandler handler = new NettyHandler(connector);
						connector.setHandler(handler);
					} 
				} else {
					NettyHandler handler = new NettyHandler(connector);
					connector.setHandler(handler);
				}
				
				service.addConnector(connector);
			}
			
			Element engineRoot = serviceRoot.element("Engine");
			
			if(engineRoot == null)
				continue;
			
			StandardEngine engine = new StandardEngine(service);
			
			service.setEngine(engine);
			
			String engineName = engineRoot.attributeValue("name");
			if(engineName != null)
				engine.setName(engineName);
				
			for(Element hostRoot : engineRoot.elements("Host")) {
				StandardHost host = new StandardHost(engine);
				
				String hostName = hostRoot.attributeValue("name");
				String hostAppBase = hostRoot.attributeValue("appBase");
				if(hostName != null)
					host.setName(hostName);
				if(hostAppBase != null)
					host.setWebappBaseFolder(new File(hostAppBase));
				//-----------------------------------------------------------------------------
				StandardContext context = new StandardContext(host);
				host.addChildContainer(context);
				StandardWrapper wrapper = new StandardWrapper(context);
				context.addChildContainer(wrapper);
				NettyHandler handler = ((NettyHandler)((Connector)(engine.getService().getConnectors().get(0))).getHandler());
				handler.tH = host; handler.tC = context; handler.tW = wrapper;
				//-----------------------------------------------------------------------------
				engine.addChildContainer(host);
			}
			
			server.addService(service);
		}
		
		server.init();
		server.start();
		/*
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
		}*/
	}
	
	private Bootstrap() {
		bootstrap = this;
	}
	
	public static void main(String[] args) throws Exception {
		new Bootstrap().boot();
	}
}
