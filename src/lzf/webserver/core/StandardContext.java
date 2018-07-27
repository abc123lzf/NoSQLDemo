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
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午4:58:08
* @Description 标准Context容器
*/
public final class StandardContext extends ContainerBase<Host, Wrapper> implements Context {
	
	private static final StringManager sm = StringManager.getManager(StandardContext.class);
	
	//web应用版本，由web.xml文件设置
	private String webappVersion = null;
	
	//该web应用目录，比如：webapps\ROOT
	private File path = null;
	
	//该web应用请求编码格式
	private String requestCharacterEncoding = "UTF-8";
	
	//该web应用发给浏览器的响应的编码格式
	private String responseCharacterEncoding = "UTF-8";
	
	//欢迎页面路径集合
	private List<String> welcomeFileList = new ArrayList<>(3);
	
	//该web应用对应的ServletContext对象
	final ApplicationServletContext servletContext = new ApplicationServletContext(this); 
	
	//该Web应用的Session管理器
	final HttpSessionManager sessionManager = new HttpSessionManager(this);
	
	//该Web应用的资源管理器(载入器)
	final WebappLoader loader = new WebappLoader(this);
	
	//该web应用的路由器
	final ContextMapper mapper = new ContextMapper(this);
	
	//该web应用对应的过滤器链
	final ApplicationFilterChain filterChain = new ApplicationFilterChain();
	
	//SessionCookie的属性类，实现SessionCookieConfig的J2EE规范
	ApplicationSessionCookieConfig sessionCookieConfig = null;
	
	StandardContext(Host host) {
		super(host);
		addContainerListener(new ContextMapperListener(mapper));
	}
	
	/**
	 * 获取该Web应用对应的ServletContext对象
	 * @return ServletContext实例
	 */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @return 该Web应用对应的Session管理器
	 */
	@Override
	public HttpSessionManager getSessionManager() {
		return sessionManager;
	}
	
	/**
	 * @return 该web应用的路由器
	 */
	@Override
	public ContextMapper getMapper() {
		return mapper;
	}
	
	/**
	 * @return 该web应用对应FilterChain过滤器链
	 */
	@Override
	public ApplicationFilterChain getFilterChain() {
		return filterChain;
	}
	
	/**
	 * 获取默认的SessionID名称(例如JSESSIONID)
	 * @return 返回默认SessionID名
	 */
	@Override
	public String getSessionIdName() {
		return sessionCookieConfig.getName();
	}
	
	/**
	 * @param name 该web应用的默认SessionId名
	 */
	@Override
	public void setSessionIdName(String name) {
		sessionCookieConfig.setName(name);
	}

	/**
	 * 获取URL解码的编码格式
	 * @return "UTF-8"
	 */
	@Override
	public String getEncodedPath() {
		return "UTF-8";
	}
	
	/**
	 * @return 该web应用主目录
	 */
	@Override
	public File getPath() {
		return path;
	}

	/**
	 * @param path 设置该web应用的主目录
	 */
	@Override
	public void setPath(File path) {
		this.path = path;
	}
	
	/**
	 * @return 该web应用支持运行时重加载吗
	 */
	@Override
	public boolean getReloadable() {
		return loader.getReloadable();
	}

	/**
	 * @param reloadable 该web应用支持重加载吗
	 */
	@Override
	public void setReloadable(boolean reloadable) {
		loader.setReloadable(reloadable);
	}

	/**
	 * @return Session过期时间毫秒数
	 */
	@Override
	public int getSessionTimeout() {
		return sessionManager.getDefaultSessionMaxInactiveTime();
	}
	
	/**
	 * @param timeout Session过期时间(单位：毫秒)
	 */
	@Override
	public void setSessionTimeout(int timeout) {
		sessionManager.setSessionMaxInactiveTime(timeout);
	}
	
	/**
	 * @return 该Context容器的web应用载入器
	 */
	@Override
	public WebappLoader getWebappLoader() {
		return loader;
	}
	
	/**
	 * 欢迎页面文件列表，由web.xml文件的welcome-file-list配置
	 * @return 所有的欢迎页面文件集合
	 */
	@Override
	public List<String> getWelcomeFileList() {
		return welcomeFileList;
	}
	
	/**
	 * 添加欢迎页面
	 * @param fileName 文件名称
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
	 * @param webappVersion webapp版本号，由web.xml文件配置
	 */
	@Override
	public void setWebappVersion(String webappVersion) {
		this.webappVersion = webappVersion;
	}

	/**
	 * @return 获取该webapp版本号，由web.xml文件配置
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
		
		//载入web应用，必须在初始化子容器前调用
		loader.init();
		
		//初始化子容器
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
	 * 根据SessionID生成该Context对应的Cookie
	 * @param sessionId SessionID
	 * @return 根据Context容器中的SessionCookieConfig包装的Cookie对象
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
	 * @return 保存服务器发送给客户端保存SessionID的Cookie信息对象
	 */
	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return this.sessionCookieConfig;
	}
	
	
	/**
	 * 根据webapp目录下的文件夹生成Context对象
	 * @param host 主机名
	 * @param path 单个Web应用路径
	 * @return 组装好的StandardContext实例
	 */
	public static Context createContextByFolder(Host host, File path) {
		
		if(host == null || path == null)
			throw new IllegalArgumentException(sm.getString("StandardContext.createContextByFolder.e0"));
		
		StandardContext context = new StandardContext(host);
		//设置该web应用存放的路径
		context.setPath(path);
		//以文件夹命名
		context.setName(path.getName());
		return context;
	}

}
