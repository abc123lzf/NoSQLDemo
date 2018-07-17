package lzf.webserver.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:53:30
* @Description 标准Server容器实现类
*/
public class StandardServer extends LifecycleBase implements Server {
	
	public static final int DEFAULT_PORT = 9005;
	public static final String DEFAULT_SHUTDOWN_CMD = "SHUTDOWN";
	
	static final byte[] SHUTDOWN_FAILURE = "Shutdown failure".getBytes();
	static final byte[] SHUTDOWN_SUCCESS = "Shutdown success".getBytes();
	
	private int port = DEFAULT_PORT;
	private String shutdownCmd = DEFAULT_SHUTDOWN_CMD;
	
	//服务器程序用户路径
	private final File mainPath = new File((String) System.getProperties().get("user.dir"));
	//保存Service组件容器
	private final List<Service> services = new CopyOnWriteArrayList<>();
	//关闭服务器指令监听线程
	private ShutdownListener shutdownListener;
	
	public StandardServer() {
	}
	       
	public StandardServer(int port) {
		this();
		this.port = port;
	}
	
	public StandardServer(int port, String shutdownCmd) {
		this(port);
		this.shutdownCmd = shutdownCmd;
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
	 * 初始化前，应先指定关闭指定端口、关闭命令字符串、设置Service对象
	 */
	@Override
	protected void initInternal() throws LifecycleException {
		try {
			shutdownListener = new ShutdownListener();
			new Thread(shutdownListener).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Service service : services)
			service.init();
	}

	@Override
	protected void startInternal() throws LifecycleException {
		for(Service service : services)
			service.start();
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		for(Service service : services)
			service.stop();
	}

	@Override
	protected void destoryInternal() throws LifecycleException {
		System.exit(0);
	}
	

	@Override
	public File getMainPath() {
		return mainPath;
	}

	@Override
	public void setPort(int port) throws LifecycleException {
		if(getLifecycleState().after(LifecycleState.INITIALIZING))
			throw new LifecycleException("无法设置端口，容器已初始化");
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
