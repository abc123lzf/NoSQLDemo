package lzf.webserver;
import java.util.List;

import lzf.webserver.connector.Connector;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:42:05
* @Description 服务层类，仅次于Server
* Service容器类应包含两种类型的组件，一个Engine组件和连接器组件
*/
public interface Service extends Lifecycle {

	/**
	 * 设置Service组件名称
	 * @param name 组件名
	 * @throws LifecycleException 当已经调用Service组件的start方法后再调用此方法
	 */
	public void setName(String name) throws LifecycleException;
	
	/**
	 * 获得该Service组件的名称
	 */
	public String getName();
	
	/**
	 * 设置父组件
	 * @param server Server实例
	 * @throws LifecycleException 当设置父组件失败时抛出此异常
	 */
	public void setServer(Server server) throws LifecycleException;
	
	/**
	 * 获得父组件：Server
	 */
	public Server getServer();
	
	/**
	 * 添加连接器实例
	 * @param connector 连接器实例
	 */
	public void addConnector(Connector connector) throws LifecycleException;
	
	/**
	 * 获得Service组件所有的连接器
	 * @return 包含所有连接器的List
	 */
	public List<Connector> getConnectors();
	
	/**
	 * 设置全局引擎容器，每个Service组件仅可设置一个全局引擎容器
	 * @param engine 容器实例
	 * @throws LifecycleException 当已经调用Service组件的start方法后再调用此方法
	 */
	public void setEngine(Engine engine) throws LifecycleException;
	
	/**
	 * 获得全局引擎容器
	 */
	public Engine getEngine();
}
