package lzf.webserver.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.mapper.ContextMapper;
import lzf.webserver.mapper.ContextMapperListener;
import lzf.webserver.session.HttpSessionManager;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����4:58:08
* @Description ��׼Context����
*/
public final class StandardContext extends ContainerBase<Host, Wrapper> implements Context {
	
	private static final StringManager sm = StringManager.getManager(StandardContext.class);
	
	//webӦ�ð汾����web.xml�ļ�����
	private String webappVersion = null;
	
	//��webӦ��Ŀ¼�����磺webapps\ROOT
	private File path = null;
	
	//��webӦ����������ʽ
	private String requestCharacterEncoding = "UTF-8";
	
	//��webӦ�÷������������Ӧ�ı����ʽ
	private String responseCharacterEncoding = "UTF-8";
	
	//��ӭҳ��·������
	private List<String> welcomeFileList = new ArrayList<>(3);
	
	//��webӦ�ö�Ӧ��ServletContext����
	final ApplicationServletContext servletContext = new ApplicationServletContext(this); 
	
	//��WebӦ�õ�Session������
	final HttpSessionManager sessionManager = new HttpSessionManager(this);
	
	//��WebӦ�õ���Դ������(������)
	final WebappLoader loader = new WebappLoader(this);
	
	//��webӦ�õ�·����
	final ContextMapper mapper = new ContextMapper(this);
	
	//��webӦ�ö�Ӧ�Ĺ�������
	final ApplicationFilterChain filterChain = new ApplicationFilterChain();
	
	//SessionCookie�������࣬ʵ��SessionCookieConfig��J2EE�淶
	ApplicationSessionCookieConfig sessionCookieConfig = null;
	
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
	 * @return ��webӦ�ö�ӦFilterChain��������
	 */
	@Override
	public ApplicationFilterChain getFilterChain() {
		return filterChain;
	}
	
	/**
	 * ��ȡĬ�ϵ�SessionID����(����JSESSIONID)
	 * @return ����Ĭ��SessionID��
	 */
	@Override
	public String getSessionIdName() {
		return sessionCookieConfig.getName();
	}
	
	/**
	 * @param name ��webӦ�õ�Ĭ��SessionId��
	 */
	@Override
	public void setSessionIdName(String name) {
		sessionCookieConfig.setName(name);
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
	
	/**
	 * @return ��Context������webӦ��������
	 */
	@Override
	public WebappLoader getWebappLoader() {
		return loader;
	}
	
	/**
	 * ��ӭҳ���ļ��б���web.xml�ļ���welcome-file-list����
	 * @return ���еĻ�ӭҳ���ļ�����
	 */
	@Override
	public List<String> getWelcomeFileList() {
		return welcomeFileList;
	}
	
	/**
	 * ��ӻ�ӭҳ��
	 * @param fileName �ļ�����
	 */
	@Override
	public void addWelcomeFile(String fileName) {
		
		if(fileName == null || fileName.equals(""))
			return;
		
		if(welcomeFileList.contains(fileName))
			return;
		
		welcomeFileList.add(fileName);
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
	protected void initInternal() throws Exception {
		
		//����webӦ�ã������ڳ�ʼ��������ǰ����
		loader.init();
		
		//��ʼ��������
		for(Wrapper wrapper: childContainers) {
			wrapper.init();
		}
		
		pipeline.addValve(new StandardContextValve());
	
		sessionManager.init();
		
		sessionCookieConfig = new ApplicationSessionCookieConfig(this);
	}

	@Override
	protected void startInternal() throws Exception {
		
		loader.start();
		
		for(Wrapper wrapper: childContainers) {
			wrapper.start();
		}
		
		sessionManager.start();
	}

	@Override
	protected void stopInternal() throws Exception {
		
		for(Wrapper wrapper: childContainers) {
			wrapper.stop();
		}
		
		sessionManager.stop();
		loader.stop();
	}

	@Override
	protected void destoryInternal() throws Exception {
		
		for(Wrapper wrapper: childContainers) {
			wrapper.destory();
		}
		
		sessionManager.destory();
		loader.stop();
	}
	
	/**
	 * ����SessionID���ɸ�Context��Ӧ��Cookie
	 * @param sessionId SessionID
	 * @return ����Context�����е�SessionCookieConfig��װ��Cookie����
	 */
	@Override
	public Cookie createSessionCookie(String sessionId) {
		
		Cookie session = new Cookie(sessionCookieConfig.getName(), sessionId);
		
		if(sessionCookieConfig.getComment() != null)
			session.setComment(sessionCookieConfig.getComment());
		
		if(sessionCookieConfig.getDomain() != null)
			session.setDomain(sessionCookieConfig.getDomain());
		
		if(sessionCookieConfig.getMaxAge() > 0)
			session.setMaxAge(sessionCookieConfig.getMaxAge());
		
		session.setPath(sessionCookieConfig.getPath());
		
		return session;
	}
	
	/**
	 * @return ������������͸��ͻ��˱���SessionID��Cookie��Ϣ����
	 */
	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return this.sessionCookieConfig;
	}
	
	
	/**
	 * ����webappĿ¼�µ��ļ�������Context����
	 * @param host ������
	 * @param path ����WebӦ��·��
	 * @return ��װ�õ�StandardContextʵ��
	 */
	public static Context createContextByFolder(Host host, File path) {
		
		if(host == null || path == null)
			throw new IllegalArgumentException(sm.getString("StandardContext.createContextByFolder.e0"));
		
		StandardContext context = new StandardContext(host);
		//���ø�webӦ�ô�ŵ�·��
		context.setPath(path);
		//���ļ�������
		context.setName(path.getName());
		return context;
	}

}
