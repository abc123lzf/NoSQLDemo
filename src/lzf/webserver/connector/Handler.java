package lzf.webserver.connector;

import java.util.concurrent.Executor;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午7:29:38
* @Description 连接接收器接口，用于接收客户端连接
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
