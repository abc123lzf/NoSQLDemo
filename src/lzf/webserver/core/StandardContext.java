package lzf.webserver.core;

import java.io.File;

import javax.servlet.ServletContext;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.loader.WebappLoader;
import lzf.webserver.mapper.ContextMapper;
import lzf.webserver.mapper.ContextMapperListener;
import lzf.webserver.session.HttpSessionManager;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����4:58:08
* @Description ��׼Context����
*/
public final class StandardContext extends ContainerBase implements Context {
	
	//Ĭ��Session ID����
	public static final String DEFAULT_SESSION_NAME = "JSESSIONID";
	
	//webӦ�ð汾����web.xml�ļ�����
	private String webappVersion = null;
	
	//��webӦ��Ŀ¼�����磺webapps\ROOT
	private File path = null;
	
	//��webӦ����������ʽ
	private String requestCharacterEncoding = "UTF-8";
	
	//��webӦ�÷������������Ӧ�ı����ʽ
	private String responseCharacterEncoding = "UTF-8";
	
	//��WebӦ��SessionID����
	private volatile String sessionIdName = DEFAULT_SESSION_NAME;
	
	//��webӦ�ö�Ӧ��ServletContext����
	final ApplicationServletContext servletContext = new ApplicationServletContext(this); 
	
	//��WebӦ�õ�Session������
	final HttpSessionManager sessionManager = new HttpSessionManager(this);
	
	//��WebӦ�õ���Դ������(������)
	final WebappLoader loader = new WebappLoader(this);
	
	//��webӦ�õ�·����
	final ContextMapper mapper = new ContextMapper(this);
	
	StandardContext(Host host) {
		super(host);
		addContainerListener(new ContextMapperListener(mapper));
	}
	
	/**
	 * ��ȡ��WebӦ�ö�Ӧ��ServletContext����
	 * @return ServletContextʵ��
	 */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @return ��WebӦ�ö�Ӧ��Session������
	 */
	@Override
	public HttpSessionManager getSessionManager() {
		return sessionManager;
	}
	
	/**
	 * @return ��webӦ�õ�·����
	 */
	@Override
	public ContextMapper getMapper() {
		return mapper;
	}
	
	/**
	 * ��ȡĬ�ϵ�SessionID����(����JSESSIONID)
	 * @return ����Ĭ��SessionID��
	 */
	@Override
	public String getSessionIdName() {
		return sessionIdName;
	}

	/**
	 * ��ȡURL����ı����ʽ
	 * @return "UTF-8"
	 */
	@Override
	public String getEncodedPath() {
		return "UTF-8";
	}
	
	/**
	 * @return ��webӦ����Ŀ¼
	 */
	@Override
	public File getPath() {
		return path;
	}

	/**
	 * @param path ���ø�webӦ�õ���Ŀ¼
	 */
	@Override
	public void setPath(File path) {
		this.path = path;
	}
	
	/**
	 * @return ��webӦ��֧������ʱ�ؼ�����
	 */
	@Override
	public boolean getReloadable() {
		return loader.getReloadable();
	}

	/**
	 * @param reloadable ��webӦ��֧���ؼ�����
	 */
	@Override
	public void setReloadable(boolean reloadable) {
		loader.setReloadable(reloadable);
	}

	/**
	 * @return Session����ʱ�������
	 */
	@Override
	public int getSessionTimeout() {
		return sessionManager.getDefaultSessionMaxInactiveTime();
	}
	
	/**
	 * @param timeout Session����ʱ��(��λ������)
	 */
	@Override
	public void setSessionTimeout(int timeout) {
		sessionManager.setSessionMaxInactiveTime(timeout);
	}
	
	@Override
	public WebappLoader getWebappLoader() {
		return loader;
	}

	/**
	 * @param webappVersion webapp�汾�ţ���web.xml�ļ�����
	 */
	@Override
	public void setWebappVersion(String webappVersion) {
		this.webappVersion = webappVersion;
	}

	/**
	 * @return ��ȡ��webapp�汾�ţ���web.xml�ļ�����
	 */
	@Override
	public String getWebappVersion() {
		return webappVersion;
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) {
		this.requestCharacterEncoding = encoding;
	}

	@Override
	public String getRequestCharacterEncoding() {
		return requestCharacterEncoding;
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		this.responseCharacterEncoding = encoding;
	}

	@Override
	public String getResponseCharacterEncoding() {
		return responseCharacterEncoding;
	}

	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException {
		if(!(container instanceof Wrapper)) {
			throw new IllegalArgumentException("Not Wrapper");
		}
	}

	@Override
	protected void initInternal() throws Exception {
		loader.init();
		
		for(Container wrapper: childContainers) {
			wrapper.init();
		}
		
		pipeline.addValve(new StandardContextValve());
		sessionManager.init();
	}

	@Override
	protected void startInternal() throws Exception {
		
		loader.start();
		
		for(Container wrapper: childContainers) {
			wrapper.start();
		}
		
		sessionManager.start();
	}

	@Override
	protected void stopInternal() throws Exception {
		
		for(Container wrapper: childContainers) {
			wrapper.stop();
		}
		
		sessionManager.stop();
		loader.stop();
	}

	@Override
	protected void destoryInternal() throws Exception {
		
		for(Container wrapper: childContainers) {
			wrapper.destory();
		}
		
		sessionManager.destory();
		loader.stop();
	}
	
	/**
	 * ����webappĿ¼�µ��ļ�������Context����
	 * @param host ������
	 * @param path ����WebӦ��·��
	 * @return ��װ�õ�StandardContextʵ��
	 */
	public static Context createContextByFolder(Host host, File path) {
		
		if(host == null || path == null)
			throw new IllegalArgumentException();
		
		StandardContext context = new StandardContext(host);
		//���ø�webӦ�ô�ŵ�·��
		context.setPath(path);
		//���ļ�������
		context.setName(path.getName());
		return context;
	}

}
