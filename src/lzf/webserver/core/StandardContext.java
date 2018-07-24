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
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午4:58:08
* @Description 标准Context容器
*/
public final class StandardContext extends ContainerBase implements Context {
	
	//默认Session ID名称
	public static final String DEFAULT_SESSION_NAME = "JSESSIONID";
	
	//web应用版本，由web.xml文件设置
	private String webappVersion = null;
	
	//该web应用目录，比如：webapps\ROOT
	private File path = null;
	
	//该web应用请求编码格式
	private String requestCharacterEncoding = "UTF-8";
	
	//该web应用发给浏览器的响应的编码格式
	private String responseCharacterEncoding = "UTF-8";
	
	//该Web应用SessionID名称
	private volatile String sessionIdName = DEFAULT_SESSION_NAME;
	
	//该web应用对应的ServletContext对象
	final ApplicationServletContext servletContext = new ApplicationServletContext(this); 
	
	//该Web应用的Session管理器
	final HttpSessionManager sessionManager = new HttpSessionManager(this);
	
	//该Web应用的资源管理器(载入器)
	final WebappLoader loader = new WebappLoader(this);
	
	//该web应用的路由器
	final ContextMapper mapper = new ContextMapper(this);
	
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
	 * 获取默认的SessionID名称(例如JSESSIONID)
	 * @return 返回默认SessionID名
	 */
	@Override
	public String getSessionIdName() {
		return sessionIdName;
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
	
	@Override
	public WebappLoader getWebappLoader() {
		return loader;
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
	 * 根据webapp目录下的文件夹生成Context对象
	 * @param host 主机名
	 * @param path 单个Web应用路径
	 * @return 组装好的StandardContext实例
	 */
	public static Context createContextByFolder(Host host, File path) {
		
		if(host == null || path == null)
			throw new IllegalArgumentException();
		
		StandardContext context = new StandardContext(host);
		//设置该web应用存放的路径
		context.setPath(path);
		//以文件夹命名
		context.setName(path.getName());
		return context;
	}

}
