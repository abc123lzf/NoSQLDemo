package lzf.webserver.connector;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.LifecycleException;
import lzf.webserver.Service;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月8日 下午3:39:40
* @Description 连接器组件，负责接收客户端连接
*/
public final class Connector extends LifecycleBase {
	
	private static final StringManager sm = StringManager.getManager(Connector.class);
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(Connector.class);
	
	public static final int DEFAULT_PORT = 9090;
	public static final int DEFAULT_TIMEOUT = 3000;
	public static final int DEFAULT_MAX_CONNECTION = 200; 
	
	private Service service;
	
	private Handler handler;
	
	private int port = DEFAULT_PORT;
	private int timeOut = DEFAULT_TIMEOUT;
	private int maxConnection = DEFAULT_MAX_CONNECTION;
	
	public Connector(Service service) {
		this.service = service;
	}

	public Service getService() {
		return service;
	}
	
	@Override
	protected void initInternal() throws Exception {
		handler.init();
	}

	@Override
	protected void startInternal() throws Exception {
		handler.start();
	}

	@Override
	protected void stopInternal() throws Exception {
		handler.stop();
	}

	@Override
	protected void destoryInternal() throws Exception {
		handler.destory();
	}

	public void setService(Service service) throws LifecycleException {
		
		if(getLifecycleState().isAvailable())
			throw new LifecycleException(sm.getString("Connector.e0", "Service"));
		
		this.service = service;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) throws LifecycleException {
		
		if(getLifecycleState().isAvailable()) {
			throw new LifecycleException(sm.getString("Connector.e0", "port"));
		}
		
		this.port = port;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) throws LifecycleException {
		
		if(getLifecycleState().isAvailable()) {
			throw new LifecycleException(sm.getString("Connector.e0", "timeOut"));
		}
		
		this.timeOut = timeOut;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) throws LifecycleException {
		
		if(getLifecycleState().isAvailable()) {
			throw new LifecycleException(sm.getString("Connector.e0", "maxConnection"));
		}
		
		this.maxConnection = maxConnection;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) throws LifecycleException {
		
		if(getLifecycleState().isAvailable()) {
			throw new LifecycleException(sm.getString("Connector.e0", "Handler"));
		}
		
		this.handler = handler;
	}
}
