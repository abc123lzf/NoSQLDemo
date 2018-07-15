package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Engine;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleListener;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.connector.Connector;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��14�� ����11:05:10
 * @Description ��׼Service��
 */
public class StandardService implements Service {

	private String name;
	private Server server;
	
	private volatile LifecycleState state;
	private LifecycleSupport lifecycleSupport = new LifecycleSupport(this);

	private List<Connector> connectors = new CopyOnWriteArrayList<>();
	private Engine engine;

	public StandardService() {
		state = LifecycleState.NEW;
	}
	
	public StandardService(String name, Engine engine) {
		this();
		this.name = name;
		this.engine = engine;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycleSupport.addLifecycleListener(listener);
	}

	@Override
	public List<LifecycleListener> getLifecycleListeners() {
		return lifecycleSupport.getLifecycleListeners();
	}

	/**
	 * ��ʼ��ǰ��Ӧ�����ø�Service�����ƣ��������һ��������������һ��Engine����
	 * �����úø����Server
	 */
	@Override
	public void init() throws LifecycleException {
		if(state.after(LifecycleState.INITIALIZING))
			throw new LifecycleException("�޷���ʼ����ǰService�������ǰ�����ʼ�����������");
		
		state = LifecycleState.INITIALIZING;
		lifecycleSupport.runLifecycleEvent(null);
		
		//TODO �������������
		//engine.init();
		
		state = LifecycleState.INITIALIZED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void start() throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�������ǰService�������ǰ���������");
		
		state = LifecycleState.STARTING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STARTING;
		//engine.start();
		
		state = LifecycleState.STARTED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void stop() throws LifecycleException {
		if(state.after(LifecycleState.STOPPING_PREP))
			throw new LifecycleException("�޷�ֹͣ��ǰService�������ǰ���������");
		
		state = LifecycleState.STOPPING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STOPPING;
		engine.stop();
		
		state = LifecycleState.STOPPED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void destory() throws LifecycleException {
		state = LifecycleState.DESTORYING;
		lifecycleSupport.runLifecycleEvent(null);
		
		engine.destory();
		
		state = LifecycleState.DESTORYED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public LifecycleState getLifecycleState() {
		return state;
	}

	@Override
	public void setName(String name) throws LifecycleException {
		if(state.after(LifecycleState.STARTED))
			throw new LifecycleException("�޷��������ƣ������������");
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setServer(Server server) throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Server���ƣ������������");
		
		this.server = server;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void addConnector(Connector connector) throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����������������������");
		
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
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Engine�����Service������");
		
		this.engine = engine;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

}
