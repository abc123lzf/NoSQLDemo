package lzf.webserver.connector;

import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月8日 下午3:39:40
* @Description 连接器组件，负责接收客户端连接
*/
public final class Connector {
	
	private Service service;
	
	private int port;
	
	private int timeOut;
	
	private int maxConnection;

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
}
