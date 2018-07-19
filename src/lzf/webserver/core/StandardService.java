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

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��14�� ����11:05:10
 * @Description ��׼Service��
 */
public class StandardService extends LifecycleBase implements Service {

	public static final String DEFAULT_NAME = "service0";
	//�����
	private String name = DEFAULT_NAME;
	//�����Serverʵ��
	private Server server;

	private GlobelMapper mapper = new GlobelMapper(this);
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
	protected void initInternal() throws Exception {
		for(Connector c : connectors)
			c.init();
	}

	@Override
	protected void startInternal() throws Exception {
		for(Connector c : connectors)
			c.start();
	}

	@Override
	protected void stopInternal() throws Exception {
		engine.stop();
	}

	@Override
	protected void destoryInternal() throws Exception {
		engine.destory();
	}

	/**
	 * ����Service�������
	 * @param name �����
	 * @throws LifecycleException ���Ѿ�����Service�����start�������ٵ��ô˷���
	 */
	@Override
	public void setName(String name) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTED))
			throw new LifecycleException("�޷��������ƣ������������");
		this.name = name;
	}
	
	/**
	 * ��ø�Service���������
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * ���ø����
	 * @param server Serverʵ��
	 * @throws LifecycleException �����ø����ʧ��ʱ�׳����쳣
	 */
	@Override
	public void setServer(Server server) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Server���ƣ������������");
		
		this.server = server;
	}

	/**
	 * ��ø������Server
	 */
	@Override
	public Server getServer() {
		return server;
	}

	/**
	 * ���������ʵ��
	 * @param connector ������ʵ��
	 */
	@Override
	public void addConnector(Connector connector) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����������������������");
		
		synchronized(connectors) {
			connectors.add(connector);
		}
	}
	
	/**
	 * ���Service������е�������
	 * @return ����������������List
	 */
	@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	/**
	 * ����ȫ������������ÿ��Service�����������һ��ȫ����������
	 * @param engine ����ʵ��
	 * @throws LifecycleException ���Ѿ�����Service�����start�������ٵ��ô˷���
	 */
	@Override
	public void setEngine(Engine engine) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�����Engine�����Service������");
		
		this.engine = engine;
	}

	/**
	 * ��ȡȫ��·�ɶ���
	 * @return GlobelMapperʵ��
	 */
	@Override
	public GlobelMapper getGlobelMapper() {
		return mapper;
	}
}
