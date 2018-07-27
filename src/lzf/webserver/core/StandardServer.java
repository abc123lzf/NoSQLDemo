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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����11:53:30
* @Description ��׼Server����ʵ����
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
	
	//�����������û�·��
	private final File mainPath = new File((String) System.getProperties().get("user.dir"));
	
	//����Service�������
	private final List<Service> services = new CopyOnWriteArrayList<>();
	
	//�رշ�����ָ������߳�
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
	 * ��������ڼ����ر�ָ������յ��ͻ��˴��͹����ĺϷ��ر�ָ��ʱ�����̻߳�������Server
	 * �����stop����ֹͣ���������еķ���
	 * ���̲߳��õ��ͻ�������ģ�͹���
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
	 * ��ʼ��ǰ��Ӧ��ָ���ر�ָ���˿ڡ��ر������ַ���������Service����
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
	 * �÷������������Service�����start����
	 */
	@Override
	protected void startInternal() throws Exception {
		for(Service service : services)
			service.start();
	}

	/**
	 * �÷������������Service�����stop����
	 */
	@Override
	protected void stopInternal() throws Exception {
		for(Service service : services)
			service.stop();
	}

	/**
	 * �÷������������Service�����destory����
	 */
	@Override
	protected void destoryInternal() throws Exception {
		for(Service service : services)
			service.destory();
		System.exit(0);
	}
	
	/**
	 * ��ȡ���������û�Ŀ¼������û�Ŀ¼�����½�Serverʵ��ʱ�Զ���ȡ
	 * ������System.getProperties().get("user.dir")��ȡ
	 * @return ���û�Ŀ¼
	 */
	@Override
	public File getMainPath() {
		return mainPath;
	}

	/**
	 * ���ü����رշ���������Ķ˿�
	 * @param port �˿ںţ�Ĭ��Ϊ9005
	 * @throws LifecycleException �������������ø÷���
	 */
	@Override
	public void setPort(int port) throws LifecycleException {
		
		if(getLifecycleState().after(LifecycleState.INITIALIZING))
			throw new LifecycleException(sm.getString("StandardServer.setPort.e0"));
		
		this.port = port;
	}
	
	/**
	 * ��ȡ�ر�����Ķ˿�
	 * @return �˿ں�
	 */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * ���ùرշ�����ָ����û��������Ĭ��ΪSHUTDOWN
	 * @param cmd �ر�ָ���Socket���������ַ���������stop()����
	 * @throws LifecycleException �������������ø÷���
	 */
	@Override
	public void setShutdownCommand(String cmd) {
		this.shutdownCmd = cmd;
	}
	
	/**
	 * ��ȡ�رշ�����ָ��
	 * @return �ر�ָ���ַ���
	 */
	@Override
	public String getShutdownCommand() {
		return shutdownCmd;
	}

	/**
	 * ����Service���
	 * @param service Serviceʵ����һ��Ϊlzf.webserver.core.StandardServer
	 * @throws LifecycleException �������������ø÷���
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
	 * ����Service���ƻ�ȡService���
	 * @param name ����
	 * @return ��Serviceʵ�������û���ҵ��򷵻�null
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
	 * ��ȡService����б�
	 * @return ���е�Service�������
	 */
	@Override
	public List<Service> getServices() {
		return services;
	}
}
