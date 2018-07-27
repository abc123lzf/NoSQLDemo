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

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Server;
import lzf.webserver.Service;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:53:30
* @Description 标准Server容器实现类
*/
public class StandardServer extends LifecycleBase implements Server {
	
	private static final Log log = LogFactory.getLog(StandardServer.class);
	
	private static final StringManager sm = StringManager.getManager(StandardServer.class);
	
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
	
	/**
	 * 这个类用于监听关闭指令，当接收到客户端传送过来的合法关闭指令时，该线程会调用这个Server
	 * 对象的stop方法停止服务器所有的服务。
	 * 该线程采用单客户端阻塞模型构建
	 */
	protected final class ShutdownListener implements Runnable {
		
		private final ServerSocket socket;
		
		public ShutdownListener() throws IOException {
			socket = new ServerSocket(port);
			//socket.setSoTimeout(180000);
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
						} catch (Exception e) {
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
						log.error(sm.getString("StandardServer.ShutdownListener.e0"), e);
					}
				}
			}
		}
	}

	/**
	 * 初始化前，应先指定关闭指定端口、关闭命令字符串、设置Service对象
	 */
	@Override
	protected void initInternal() throws Exception {
		try {
			shutdownListener = new ShutdownListener();
			new Thread(shutdownListener).start();
		} catch (IOException e) {
			log.error(sm.getString("StandardServer.initInternal.e0"), e);
		}
		
		for(Service service : services)
			service.init();
	}

	/**
	 * 该方法会调用所有Service组件的start方法
	 */
	@Override
	protected void startInternal() throws Exception {
		for(Service service : services)
			service.start();
	}

	/**
	 * 该方法会调用所有Service组件的stop方法
	 */
	@Override
	protected void stopInternal() throws Exception {
		for(Service service : services)
			service.stop();
	}

	/**
	 * 该方法会调用所有Service组件的destory方法
	 */
	@Override
	protected void destoryInternal() throws Exception {
		for(Service service : services)
			service.destory();
		System.exit(0);
	}
	
	/**
	 * 获取服务器主用户目录，这个用户目录会在新建Server实例时自动获取
	 * 即调用System.getProperties().get("user.dir")获取
	 * @return 主用户目录
	 */
	@Override
	public File getMainPath() {
		return mainPath;
	}

	/**
	 * 设置监听关闭服务器命令的端口
	 * @param port 端口号，默认为9005
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	@Override
	public void setPort(int port) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.INITIALIZING))
			throw new LifecycleException(sm.getString("StandardServer.setPort.e0"));
		
		this.port = port;
	}
	
	/**
	 * 获取关闭命令的端口
	 * @return 端口号
	 */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * 设置关闭服务器指令，如果没有设置则默认为SHUTDOWN
	 * @param cmd 关闭指令，当Socket监听到该字符串后会调用stop()方法
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	@Override
	public void setShutdownCommand(String cmd) {
		this.shutdownCmd = cmd;
	}
	
	/**
	 * 获取关闭服务器指令
	 * @return 关闭指令字符串
	 */
	@Override
	public String getShutdownCommand() {
		return shutdownCmd;
	}

	/**
	 * 设置Service组件
	 * @param service Service实例，一般为lzf.webserver.core.StandardServer
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	@Override
	public void addService(Service service) throws LifecycleException {
		
		String name = service.getName();
		
		for(Service s : services) {
			if(s.getName().equals(name))
				throw new IllegalArgumentException(sm.getString("StandardServer.addService.e0", service.getName()));
		}
		
		synchronized(services) {
			services.add(service);
		}
	}
	
	/**
	 * 根据Service名称获取Service组件
	 * @param name 名称
	 * @return 该Service实例，如果没有找到则返回null
	 */
	@Override
	public Service getService(String name) {
		
		for(Service service : services) {
			if(service.getName().equals(name))
				return service;
		}
		
		return null;
	}

	/**
	 * 获取Service组件列表
	 * @return 所有的Service组件集合
	 */
	@Override
	public List<Service> getServices() {
		return services;
	}
}
