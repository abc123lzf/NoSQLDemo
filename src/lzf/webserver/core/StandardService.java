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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��14�� ����11:05:10
 * @Description ��׼Service��
 */
public class StandardService extends LifecycleBase implements Service {

	//�����
	private String name;
	//�����Serverʵ��
	private Server server;

	//����������
	private List<Connector> connectors = new CopyOnWriteArrayList<>();
	//Engine���
	private Engine engine;

	public StandardService() {
	}
	
	public StandardService(String name, Engine engine) {
		this();
		this.name = name;
		this.engine = engine;
	}
	
	/**
	 * ��ʼ��ǰ��Ӧ�����ø�Service�����ƣ��������һ��������������һ��Engine����
	 * �����úø����Server
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
			throw new LifecycleException("�޷��������ƣ������������");
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setServer(Server server) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Server���ƣ������������");
		
		this.server = server;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void addConnector(Connector connector) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
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
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Engine�����Service������");
		
		this.engine = engine;
	}
}
