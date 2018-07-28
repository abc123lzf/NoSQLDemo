package lzf.webserver.connector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import lzf.webserver.util.IteratorEnumeration;
import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.LifecycleException;
import lzf.webserver.Wrapper;
import lzf.webserver.core.ApplicationRequestDispatcher;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:45:14
* @Description HTTP请求类，由连接器进行封装
*/
public abstract class Request extends RequestBase {
	
	public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy",Locale.US);
	
	Response response;
	
	private static final Log log = LogFactory.getLog(Request.class);
	
	private int localPort = 80;
	
	private String localName = null;
	
	private String localAddr = null;
	
	//从Cookie或URL获取的sessionID(不是容器中的SessionID)
	private String sessionId;
	
	//上面SessionID是从Cookie获取的吗
	private boolean sessionFromCookie = false;
	
	//上面SessionID是从URL获取的吗
	private boolean sessionFromURL = false;
	
	//属性Map
	protected final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	//该Request被路由到的Host容器
	protected Host host = null;
	
	//该Request被路由到的Context容器
	protected Context context = null;
	
	//该Wrapper被路由到的Wrapper容器
	protected Wrapper wrapper = null;

	//Session管理器中的HttpSession实例
	private HttpSession session = null;
	
	private DispatcherType dispatcherType = DispatcherType.REQUEST;
	
	/**
	 * 获取属性值
	 * @param 属性名
	 * @return 属性对象
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * 获取属性Map集合中所有属性名的迭代器
	 * @return Enumeration迭代器
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<String>(attributeMap.keySet().iterator());
	}

	
	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		characterEncoding = env;
	}

	/**
	 * 设置属性值
	 * @param name 属性名
	 * @param obj 属性对象
	 */
	@Override
	public void setAttribute(String name, Object obj) {
		
		Object value = attributeMap.get(name);
		attributeMap.put(name, obj);
		
		if(value == null) {
			context.getListenerContainer().runRequestAttributeAddedEvent(this, name, value);
		} else {
			context.getListenerContainer().runRequestAttributeReplacedEvent(this, name, value);
		}
	}

	/**
	 * 通过属性名移除属性
	 * @param name 需要移除的属性名
	 */
	@Override
	public void removeAttribute(String name) {
		
		Object value = attributeMap.get(name);
		
		if(value != null) {
			attributeMap.remove(name);
			context.getListenerContainer().runRequestAttributeRemovedEvent(this, name, value);
		}
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		
		if(context == null || path == null)
			return null;
		
		return new ApplicationRequestDispatcher(context, path, this);
	}

	/**
	 * 获取项目运行目录(完整磁盘路径)
	 * 该方法已废除，建议通过getServletContext().getRealPath(path)获取
	 * @param path 绝对路径
	 * @return 完整磁盘路径
	 */
	@Override @Deprecated
	public String getRealPath(String path) {
		return getServletContext().getRealPath(path);
	}

	/**
	 * 获取发出请求的客户端的主机名
	 */
	@Override
	public String getLocalName() {
		return localName;
	}

	/**
	 * 获取发出请求的客户端的IP地址
	 * @return IP地址字符串
	 */
	@Override
	public String getLocalAddr() {
		return localAddr;
	}

	/**
	 * 获得该Web服务器接收请求的端口
	 */
	@Override
	public int getLocalPort() {
		return localPort;
	}

	@Override
	public ServletContext getServletContext() {
		return context.getServletContext();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncStarted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取服务端跳转类型，默认为Request，当调用getRequestDispatcher的forward方法或include方法时，会修改该
	 * 对象的值
	 * @return 服务端跳转类型对象
	 */
	@Override
	public DispatcherType getDispatcherType() {
		return dispatcherType;
	}
	
	public void setDispatcherType(DispatcherType type) {
		this.dispatcherType = type;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		
		String date = getHeader(name);
		
		if(date == null) {
			return -1;
		}
		
		try {
			Date d = HTTP_DATE_FORMAT.parse(date);
			return d.getTime();
		} catch (ParseException e) {
			log.error("时间转换异常", e);
			return -1;
		}
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		return context.getPath().getPath();
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 返回所请求的Servlet的真实磁盘路径
	 * 若请求的Servlet不存在则返回null
	 */
	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 从HTTP请求中获得SessionID值
	 * 通过Cookie或URL参数中获取SessionID
	 */
	@Override
	public String getRequestedSessionId() {
		//先从Cookie中查找Session
		Cookie[] cookies = getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(context.getSessionIdName())) {
				this.sessionFromCookie = true;
				return cookie.getValue();
			}
		}
		//再从URI参数中寻找
		String sessionId = super.getParameter(context.getSessionIdName());
		if(sessionId != null) {
			this.sessionFromURL = true;
			return sessionId;
		}
		//如果都没有找到则返回null
		return null;
	}

	/**
	 * 获取Session对象
	 * @param create true:如果没有找到则创建一个新的Session false:如果没有找到则返回null
	 */
	@Override
	public HttpSession getSession(boolean create) {
		if(create)
			return getSession();
		
		//从URL和Cookie中查找SessionID
		if(this.sessionId == null)
			this.sessionId = getRequestedSessionId();
		
		//如果没有找到则返回null
		if(this.sessionId == null)
			return null;
		
		//如果从URL和Cookie中找到SessionID则从Session管理器查找该Session对象，该返回值可能为null，
		//如果用户是第一次访问页面或者会话已过期
		try {
			session = context.getSessionManager().getHttpSession(sessionId, false);
		} catch (LifecycleException e) {
			log.error("Session管理器不可用", e);
		}
		return session;
	}

	/**
	 * 根据请求中的Session字段获取Session对象，如果没有找到则创建一个新的Session
	 * 等同于getSession(true)
	 */
	@Override
	public HttpSession getSession() {
		
		if(session != null)
			return session;
		
		if(this.sessionId == null)
			this.sessionId = getRequestedSessionId();
		
		try {
			if(sessionId == null) {
				session = context.getSessionManager().getHttpSession(null, true);
			} else {
				session = context.getSessionManager().getHttpSession(sessionId, true);
			}
			return this.session;
			
		} catch (LifecycleException e) {
			log.error("Session管理器不可用", e);
			return null;
		}
	}

	@Override
	public String changeSessionId() {
		try {
			if(session != null)
				return context.getSessionManager().changeSessionId(session.getId());
			
			return context.getSessionManager().changeSessionId(getSession().getId());
		} catch(LifecycleException e) {
			log.error("Session管理器不可用", e);
			return null;
		}
	}

	/**
	 * 判断从URL或Cookie中的提取的会话ID在容器里是否过期
	 * @return 会话过期了吗？
	 */
	@Override
	public boolean isRequestedSessionIdValid() {
		
		if(this.sessionId == null)
			getRequestedSessionId();
		try {
			if(context.getSessionManager().getSession(sessionId, false) == null)
				return true;
			return false;
		} catch(Exception e) {
			log.error("Session管理器不可用", e);
			return true;
		}
	}

	/**
	 * 判断从URL或Cookie中的提取的会话ID来自Cooike吗
	 * @return 来自Cookie吗？
	 */
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		
		if(this.sessionId == null)
			getRequestedSessionId();
		return sessionFromCookie;
	}

	/**
	 * 判断从URL或Cookie中的提取的会话ID来自URL吗
	 * @return 来自URL吗？
	 */
	@Override
	public boolean isRequestedSessionIdFromURL() {
		
		if(this.sessionId == null)
			getRequestedSessionId();
		return sessionFromURL;
	}

	/**
	 * 判断从URL或Cookie中的提取的会话ID来自Cooike吗
	 * @return 来自URL吗？
	 */
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return 该Request被路由到的Host容器
	 */
	public Host getHost() {
		return this.host;
	}
	
	/**
	 * @return 该Request被路由到的Context容器
	 */
	public Context getContext() {
		return this.context;
	}
	
	/**
	 * @return 该Request被路由到的Wrapper容器
	 */
	public Wrapper getWrapper() {
		return this.wrapper;
	}
	
	public void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	/**
	 * 重新设置URI参数，供服务端forward跳转使用
	 * @param uri URI路径，包括参数
	 */
	public void setRequestURI(String uri) {
		this.requestUrl = uri;
	}
	
	/**
	 * 清空URI附带的参数，以便forward跳转
	 */
	public void cleanParameterMap() {
		parameterMap.clear();
	}

	/**
	 * @return 与之伴随的Response对象
	 */
	public Response getResponse() {
		return response;
	}

}