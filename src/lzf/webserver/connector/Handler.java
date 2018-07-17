package lzf.webserver.connector;

import java.util.concurrent.Executor;

import lzf.webserver.Lifecycle;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午7:29:38
* @Description 连接接收器接口，用于接收客户端连接
*/
public interface Handler extends Lifecycle {
	
	/**
	 * 设置该接收器所属的连接器
	 * @param connector 连接器实例
	 */
	public void setConnector(Connector connector);
	
	/**
	 * 返回该接收器所属的连接器
	 * @return 连接器实例
	 */
	public Connector getConnector();
	
	/**
	 * 返回该接收器所属的线程池
	 * @return Executor线程池
	 */
	public Executor getExecutor();
	
}
