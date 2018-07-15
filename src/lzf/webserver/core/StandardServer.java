package lzf.webserver.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleListener;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����11:53:30
* @Description ��׼Server����ʵ����
*/
public class StandardServer implements Server {
	
	public static final int DEFAULT_PORT = 9005;
	public static final String DEFAULT_SHUTDOWN_CMD = "SHUTDOWN";
	
	static final byte[] SHUTDOWN_FAILURE = "Shutdown failure".getBytes();
	static final byte[] SHUTDOWN_SUCCESS = "Shutdown success".getBytes();
	
	private int port = DEFAULT_PORT;
	private String shutdownCmd = DEFAULT_SHUTDOWN_CMD;
	
	private volatile LifecycleState state;
	
	//�����������û�·��
	private final File mainPath = new File((String) System.getProperties().get("user.dir"));
	//����Service�������
	private final List<Service> services = new CopyOnWriteArrayList<>();
	//������������֧����
	private final LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
	//�رշ�����ָ������߳�
	private ShutdownListener shutdownListener;
	
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
	
	protected final class ShutdownListener implements Runnable {

		private final ServerSocket socket;
		
		public ShutdownListener() throws IOException {
			socket = new ServerSocket(port);
			socket.setSoTimeout(180000);
		}
		
		@Override
		public void run() {
			while(true) {
				Socket client = null;
				InputStream is = null;
				try {
					client = socket.accept();
					is = client.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					char[] buf = new char[512];
					br.read(buf);
					String str = String.valueOf(buf);
					
					if(str.equals(shutdownCmd)) {
						OutputStream os = client.getOutputStream();
						try {
							stop();
							os.write(SHUTDOWN_SUCCESS);
							break;
						} catch (LifecycleException e) {
							os.write(SHUTDOWN_FAILURE);
						} finally {
							os.close();
						}
					}
				} catch (IOException e) {
				} finally {
					try {
						is.close();
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * ��ʼ��ǰ��Ӧ��ָ���ر�ָ���˿ڡ��ر������ַ���������Service����
	 */
	@Override
	public void init() throws LifecycleException {
		if(state.after(LifecycleState.INITIALIZING))
			throw new LifecycleException("�޷���ʼ����ǰServer�������ǰ�����ʼ�����������");
		
		state = LifecycleState.INITIALIZING;
		lifecycleSupport.runLifecycleEvent(null);
		
		try {
			shutdownListener = new ShutdownListener();
			new Thread(shutdownListener).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Service service : services)
			service.init();
		
		state = LifecycleState.INITIALIZED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	/**
	 * ����Server�������������������Lifecycle��Ա������start����
	 */
	@Override
	public void start() throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException("�޷�������ǰServer�������ǰ���������");
		
		state = LifecycleState.STARTING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STARTING;
		for(Service service : services)
			service.start();
		
		state = LifecycleState.STARTED;
		lifecycleSupport.runLifecycleEvent(null);
	}

	/**
	 * ֹͣ�����������������������Lifecycle��Ա������stop����
	 */
	@Override
	public void stop() throws LifecycleException {
		if(state.after(LifecycleState.STOPPING_PREP))
			throw new LifecycleException("�޷�ֹͣ��ǰServer�������ǰ����Ѿ�ֹͣ");
		
		state = LifecycleState.STOPPING_PREP;
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
	public void setPort(int port) throws LifecycleException {
		if(state.after(LifecycleState.INITIALIZING))
			throw new LifecycleException("�޷����ö˿ڣ������ѳ�ʼ��");
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
	public void addService(Service service) throws LifecycleException {
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
