package lzf.webserver.connector;

import lzf.webserver.Service;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��8�� ����3:39:40
* @Description �����������������տͻ�������
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
