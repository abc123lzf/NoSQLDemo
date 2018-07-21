package lzf.webserver.core;

import javax.servlet.ServletContext;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.session.HttpSessionManager;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午4:58:08
* @Description 类说明
*/
public final class StandardContext extends ContainerBase implements Context {
	
	public static final String DEFAULT_SESSION_NAME = "JSESSIONID";
	
	private String webappVersion = null;
	
	private String path = null;
	
	private String requestCharacterEncoding = "UTF-8";
	
	private String responseCharacterEncoding = "UTF-8";
	
	private volatile String sessionIdName = DEFAULT_SESSION_NAME;
	
	private boolean reloadable = true;
	
	private final ApplicationServletContext servletContext = new ApplicationServletContext(this); 
	
	private final HttpSessionManager sessionManager = new HttpSessionManager(this);
	
	public StandardContext(Host host) {
		super.parentContainer = host;
	}
	
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public HttpSessionManager getSessionManager() {
		return sessionManager;
	}

	@Override
	public String getSessionIdName() {
		return sessionIdName;
	}

	@Override
	public String getEncodedPath() {
		return "UTF-8";
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean getReloadable() {
		return reloadable;
	}

	@Override
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	@Override
	public int getSessionTimeout() {
		return sessionManager.getDefaultSessionMaxInactiveTime();
	}

	@Override
	public void setSessionTimeout(int timeout) {
		sessionManager.setSessionMaxInactiveTime(timeout);
	}

	@Override
	public void setWebappVersion(String webappVersion) {
		this.webappVersion = webappVersion;
	}

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
		for(Container wrapper: childContainers) {
			wrapper.init();
		}
		pipeline.addValve(new StandardContextValve());
		//启动Session管理器
	}

	@Override
	protected void startInternal() throws Exception {
		for(Container wrapper: childContainers) {
			wrapper.start();
		}
	}

	@Override
	protected void stopInternal() throws Exception {
		for(Container wrapper: childContainers) {
			wrapper.stop();
		}
	}

	@Override
	protected void destoryInternal() throws Exception {
		for(Container wrapper: childContainers) {
			wrapper.destory();
		}
	}
}
