package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Engine;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月14日 上午11:05:10
 * @Description 标准Service类
 */
public class StandardService extends LifecycleBase implements Service {

	//组件名
	private String name;
	//父组件Server实例
	private Server server;

	//连接器集合
	private List<Connector> connectors = new CopyOnWriteArrayList<>();
	//Engine组件
	private Engine engine;

	public StandardService() {
	}
	
	public StandardService(String name, Engine engine) {
		this();
		this.name = name;
		this.engine = engine;
	}
	
	/**
	 * 初始化前，应先设置该Service的名称，添加至少一个连接器、至少一个Engine容器
	 * 并设置好父组件Server
	 */
	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	protected void initInternal() throws LifecycleException {
		for(Connector c : connectors)
			c.init();
	}

	@Override
	protected void startInternal() throws LifecycleException {
		for(Connector c : connectors)
			c.start();
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		engine.stop();
	}

	@Override
	protected void destoryInternal() throws LifecycleException {
		engine.destory();
	}


	@Override
	public void setName(String name) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTED))
			throw new LifecycleException("无法设置名称：该组件已启用");
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setServer(Server server) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("无法设置Server名称：该组件已启动");
		
		this.server = server;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void addConnector(Connector connector) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("无法添加连接器：该组件已启动");
		
		synchronized(connectors) {
			connectors.add(connector);
		}
	}

	@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	@Override
	public void setEngine(Engine engine) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("无法设置Engine组件：Service已启动");
		
		this.engine = engine;
	}
}
