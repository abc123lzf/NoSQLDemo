package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Engine;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;
import lzf.webserver.mapper.GlobelMapper;
import lzf.webserver.util.StringManager;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月14日 上午11:05:10
 * @Description 标准Service类
 */
public class StandardService extends LifecycleBase implements Service {
	
	private static final StringManager sm = StringManager.getManager(StandardService.class);

	public static final String DEFAULT_NAME = "service0";
	//组件名
	private String name = DEFAULT_NAME;
	//父组件Server实例
	private Server server;

	private GlobelMapper mapper = new GlobelMapper(this);
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
	protected void initInternal() throws Exception {
		if(engine != null)
			engine.init();
		for(Connector c : connectors)
			c.init();
	}

	@Override
	protected void startInternal() throws Exception {
		if(engine != null)
			engine.start();
		for(Connector c : connectors)
			c.start();
	}

	@Override
	protected void stopInternal() throws Exception {
		if(engine != null)
			engine.stop();
		engine.stop();
	}

	@Override
	protected void destoryInternal() throws Exception {
		if(engine != null)
			engine.destory();
		engine.destory();
	}

	/**
	 * 设置Service组件名称
	 * @param name 组件名
	 * @throws LifecycleException 当已经调用Service组件的start方法后再调用此方法
	 */
	@Override
	public void setName(String name) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTED))
			throw new LifecycleException(sm.getString("StandardService.setName.e0", name));
		
		this.name = name;
	}
	
	/**
	 * 获得该Service组件的名称
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * 设置父组件
	 * @param server Server实例
	 * @throws LifecycleException 当设置父组件失败时抛出此异常
	 */
	@Override
	public void setServer(Server server) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardService.setServer.e0", name));
		
		this.server = server;
	}

	/**
	 * 获得父组件：Server
	 */
	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * 添加连接器实例
	 * @param connector 连接器实例
	 */
	@Override
	public void addConnector(Connector connector) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardService.addConnector.e0", name));
		
		synchronized(connectors) {
			connectors.add(connector);
		}
	}
	
	/**
	 * 获得Service组件所有的连接器
	 * @return 包含所有连接器的List
	 */
	@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	/**
	 * 设置全局引擎容器，每个Service组件仅可设置一个全局引擎容器
	 * @param engine 容器实例
	 * @throws LifecycleException 当已经调用Service组件的start方法后再调用此方法
	 */
	@Override
	public void setEngine(Engine engine) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("StandardService.setEnginer.e0", name));
		
		this.engine = engine;
	}

	/**
	 * 获取全局路由对象
	 * @return GlobelMapper实例
	 */
	@Override
	public GlobelMapper getGlobelMapper() {
		return mapper;
	}
}
