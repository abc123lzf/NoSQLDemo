package lzf.webserver.connector;

import java.util.concurrent.Executor;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����7:29:38
* @Description ���ӽ������ӿڣ����ڽ��տͻ�������
*/
public interface Handler {
	
	public void setConnector(Connector connector);
	
	public Connector getConnector();
	
	public void init() throws HandlerException;
	
	public void start() throws HandlerException;
	
	public void stop() throws HandlerException;
	
	public Executor getExecutor();
	
	public HandlerState getState();
}
