package lzf.webserver.startup;

import java.io.File;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import lzf.webserver.LifecycleException;
import lzf.webserver.Server;
import lzf.webserver.connector.Connector;
import lzf.webserver.connector.NettyHandler;
import lzf.webserver.core.StandardEngine;
import lzf.webserver.core.StandardHost;
import lzf.webserver.core.StandardServer;
import lzf.webserver.core.StandardService;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.StringManager;
import lzf.webserver.util.XMLUtil;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月8日 下午3:28:20
 * @Description 启动类
 */
public final class Bootstrap {

	private static final StringManager sm = StringManager.getManager(Bootstrap.class);
	
	private static final Log log = LogFactory.getLog(Bootstrap.class);

	@SuppressWarnings("unused")
	private static Bootstrap bootstrap;

	public void boot() throws Exception {
		
		log.info(sm.getString("Bootstrap.boot.i0"));
		
		long st = System.currentTimeMillis();

		Server server = loadServerXml();
		server.init();
		server.start();
		
		long ed = System.currentTimeMillis();
		
		log.info(sm.getString("Bootstrap.boot.i1", ed - st));
	}

	private Server loadServerXml() throws NumberFormatException, LifecycleException {
		// 读取server.xml文件并生成相应的对象
		Element serverRoot;

		try {
			serverRoot = XMLUtil.getXMLRoot(ServerConstant.getConstant().getServerXml());
		} catch (DocumentException e) {
			log.error(sm.getString("Bootstrap.loadServerXml.e0"), e);
			return null;
		}

		StandardServer server = new StandardServer();

		// 设置Server的关闭指令接收端口
		String shutdownPortStr = serverRoot.attributeValue("port");

		if (shutdownPortStr != null)
			server.setPort(Integer.valueOf(shutdownPortStr));

		// 设置关闭指令
		String shutdownCmd = serverRoot.attributeValue("shutdown");

		if (shutdownCmd != null)
			server.setShutdownCommand(shutdownCmd);

		// 遍历Server节点下的Service节点
		for (Element serviceRoot : serverRoot.elements("Service")) {

			StandardService service = new StandardService();

			// 设置Service的名称
			String serviceName = serviceRoot.attributeValue("name");

			if (serviceName != null)
				service.setName(serviceName);

			// 遍历Service节点下的Connector节点
			for (Element connectorRoot : serviceRoot.elements("Connector")) {

				Connector connector = new Connector(service);

				// 设置连接器绑定端口
				String connectorPort = connectorRoot.attributeValue("port");

				// 设置连接器与客户端最大非活跃连接时长
				String connectionTimeout = connectorRoot.attributeValue("connectionTimeout");

				// 设置Socket接收器类型，目前仅支持Netty的NIO模式
				String handlerType = connectorRoot.attributeValue("handler");

				if (connectorPort != null)
					connector.setPort(Integer.valueOf(connectorPort));

				if (connectionTimeout != null)
					connector.setTimeOut(Integer.valueOf(connectionTimeout));

				if (handlerType != null) {
					if (handlerType.toLowerCase().equals("netty")) {
						NettyHandler handler = new NettyHandler(connector);
						connector.setHandler(handler);
					}
				} else {
					NettyHandler handler = new NettyHandler(connector);
					connector.setHandler(handler);
				}

				service.addConnector(connector);
			}

			// 获取Service对象的Engine节点，Engine节点只能拥有一个
			Element engineRoot = serviceRoot.element("Engine");

			if (engineRoot == null)
				continue;

			StandardEngine engine = new StandardEngine(service);

			service.setEngine(engine);

			String engineName = engineRoot.attributeValue("name");

			if (engineName != null)
				engine.setName(engineName);

			// 遍历Engine节点下的Host节点，Host节点在初始化时会自动加载目录下的web应用
			for (Element hostRoot : engineRoot.elements("Host")) {

				StandardHost host = new StandardHost(engine);

				String hostName = hostRoot.attributeValue("name");
				String hostAppBase = hostRoot.attributeValue("appBase");
				
				if (hostName != null)
					host.setName(hostName);
				
				if (hostAppBase != null)
					host.setWebappBaseFolder(new File(hostAppBase));
				// -----------------------------------------------------------------------------
				
				// -----------------------------------------------------------------------------
				engine.addChildContainer(host);
			}

			server.addService(service);
		}
		
		return server;
	}

	private Bootstrap() {
		bootstrap = this;
	}

	public static void main(String[] args) throws Exception {
		new Bootstrap().boot();
	}
}
