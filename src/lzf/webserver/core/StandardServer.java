package lzf.webserver.core;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleListener;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:53:30
* @Description 标准Server容器实现类
*/
public class StandardServer implements Server {
	
	public static final int DEFAULT_PORT = 9005;
	public static final String DEFAULT_SHUTDOWN_CMD = "SHUTDOWN";
	
	private int port = DEFAULT_PORT;
	private String shutdownCmd = DEFAULT_SHUTDOWN_CMD;
	
	private LifecycleState state;
	
	//服务器程序用户路径
	private final File mainPath = new File((String) System.getProperties().get("user.dir"));
	//保存Service组件容器
	private final List<Service> services = new CopyOnWriteArrayList<>();
	//创建生命周期支持类
	private final LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
	
	public StandardServer() {
		state = LifecycleState.NEW;
	}
	       
	public StandardServer(int port) {
		this();
		this.port = port;
	}
	
	public StandardServer(int port, String shutdownCmd) {
		this(port);
		this.shutdownCmd = shutdownCmd;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycleSupport.addLifecycleListener(listener);
	}

	@Override
	public List<LifecycleListener> getLifecycleListeners() {
		return lifecycleSupport.getLifecycleListeners();
	}

	@Override
	public void init() throws LifecycleException {
		if(state.getStep() >= LifecycleState.INITIALIZING.getStep())
			throw new LifecycleException("this part init step is complete.");
		
		state = LifecycleState.INITIALIZING;
		lifecycleSupport.runLifecycleEvent(null);
		
		for(Service service : services)
			service.init();
		
		state = LifecycleState.INITIALIZED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void start() throws LifecycleException {
		if(state.getStep() >= LifecycleState.STARTING_PREP.getStep())
			throw new LifecycleException("this part start step is complete");
		
		state = LifecycleState.STARTING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STARTING;
		for(Service service : services)
			service.start();
		
		state = LifecycleState.STARTED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void stop() throws LifecycleException {
		if(state.getStep() >= LifecycleState.STOPING_PREP.getStep())
			throw new LifecycleException("this part stop step is complete");
		
		state = LifecycleState.STOPING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STARTING;
		for(Service service : services)
			service.stop();
		
		state = LifecycleState.STOPPED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	@Override
	public void destory() throws LifecycleException {
		System.exit(0);
	}

	@Override
	public LifecycleState getLifecycleState() {
		return state;
	}

	@Override
	public File getMainPath() {
		return mainPath;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setShutdownCommand(String cmd) {
		this.shutdownCmd = cmd;
	}

	@Override
	public String getShutdownCommand() {
		return shutdownCmd;
	}

	@Override
	public void setService(Service service) {
		String name = service.getName();
		for(Service s : services) {
			if(s.getName().equals(name))
				throw new IllegalArgumentException("This service part is exists.");
		}
		synchronized(services) {
			services.add(service);
		}
	}

	@Override
	public Service getService(String name) {
		for(Service service : services) {
			if(service.getName().equals(name))
				return service;
		}
		return null;
	}

	@Override
	public List<Service> getServices() {
		return services;
	}
}
