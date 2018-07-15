package lzf.webserver.connector;

import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月8日 下午3:39:40
* @Description 连接器组件，负责接收客户端连接
*/
public final class Connector {
	
	//
	public static final int DEFAULT_PORT = 9090;
	public static final int DEFAULT_TIMEOUT = 20000;
	public static final int DEFAULT_MAX_CONNECTION = 200; 
	
	private Service service;
	
	private Handler handler;
	
	private int port = DEFAULT_PORT;
	private int timeOut = DEFAULT_TIMEOUT;
	private int maxConnection = DEFAULT_MAX_CONNECTION;
	
	public void init() {
		try {
			handler.init();
		} catch (HandlerException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			handler.start();
		} catch (HandlerException e) {
			e.printStackTrace();
		}
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
