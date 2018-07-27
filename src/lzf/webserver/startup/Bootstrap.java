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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��8�� ����3:28:20
 * @Description ������
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
		// ��ȡserver.xml�ļ���������Ӧ�Ķ���
		Element serverRoot;

		try {
			serverRoot = XMLUtil.getXMLRoot(ServerConstant.getConstant().getServerXml());
		} catch (DocumentException e) {
			log.error(sm.getString("Bootstrap.loadServerXml.e0"), e);
			return null;
		}

		StandardServer server = new StandardServer();

		// ����Server�Ĺر�ָ����ն˿�
		String shutdownPortStr = serverRoot.attributeValue("port");

		if (shutdownPortStr != null)
			server.setPort(Integer.valueOf(shutdownPortStr));

		// ���ùر�ָ��
		String shutdownCmd = serverRoot.attributeValue("shutdown");

		if (shutdownCmd != null)
			server.setShutdownCommand(shutdownCmd);

		// ����Server�ڵ��µ�Service�ڵ�
		for (Element serviceRoot : serverRoot.elements("Service")) {

			StandardService service = new StandardService();

			// ����Service������
			String serviceName = serviceRoot.attributeValue("name");

			if (serviceName != null)
				service.setName(serviceName);

			// ����Service�ڵ��µ�Connector�ڵ�
			for (Element connectorRoot : serviceRoot.elements("Connector")) {

				Connector connector = new Connector(service);

				// �����������󶨶˿�
				String connectorPort = connectorRoot.attributeValue("port");

				// ������������ͻ������ǻ�Ծ����ʱ��
				String connectionTimeout = connectorRoot.attributeValue("connectionTimeout");

				// ����Socket���������ͣ�Ŀǰ��֧��Netty��NIOģʽ
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

			// ��ȡService�����Engine�ڵ㣬Engine�ڵ�ֻ��ӵ��һ��
			Element engineRoot = serviceRoot.element("Engine");

			if (engineRoot == null)
				continue;

			StandardEngine engine = new StandardEngine(service);

			service.setEngine(engine);

			String engineName = engineRoot.attributeValue("name");

			if (engineName != null)
				engine.setName(engineName);

			// ����Engine�ڵ��µ�Host�ڵ㣬Host�ڵ��ڳ�ʼ��ʱ���Զ�����Ŀ¼�µ�webӦ��
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
