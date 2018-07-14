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

	public void setName(String name);
	
	public String getName();
	
	public Server getServer();
	
	public void setConnector(Connector connector);
	
	public List<Connector> getConnectors();
	
	public void setEngine();
}
