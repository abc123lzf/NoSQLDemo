package lzf.webserver.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import lzf.webserver.util.IteratorEnumeration;
import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月12日 下午1:45:14
* @Description HTTP请求类，由连接器进行封装
*/
public class Request extends RequestBase {
	
	private Context context;
	
	private String sessionId;
	
	protected final Map<String, Object> attributeMap = new ConcurrentHashMap<>();

	protected String characterEncoding = null;
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<String>(attributeMap.keySet().iterator());
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.characterEncoding = env;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object obj) {
		attributeMap.put(name, obj);
	}

	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override @Deprecated
	public String getRealPath(String path) {
		return getServletContext().getRealPath(path);
	}

	/**
	 * 获取发出请求的客户端的主机名
	 */
	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取发出请求的客户端的IP地址
	 * @return IP地址字符串
	 */
	@Override
	public String getLocalAddr() {
		return null;
	}

	/**
	 * 获得该Web服务器接收请求的端口
	 */
	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
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

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
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
		Cookie[] cookies = getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(context.getSessionIdName())) {
				return cookie.getName();
			}
		}
		String sessionId = super.getParameter(context.getSessionIdName());
		if(sessionId != null)
			return sessionId;
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
		//如果从从URL和Cookie中找到SessionID则从Session管理器查找该Session对象
		return context.getSessionManager().getHttpSession(sessionId, false);
	}

	/**
	 * 获取Session对象，如果没有找到则创建一个新的Session
	 * 等同于getSession(true)
	 */
	@Override
	public HttpSession getSession() {
		if(this.sessionId == null)
			this.sessionId = getRequestedSessionId();
		if(sessionId == null) {
			return context.getSessionManager().getHttpSession(sessionId, true);
		} else {
			return context.getSessionManager().getHttpSession(sessionId, true);
		}
	}

	@Override
	public String changeSessionId() {
		return context.getSessionManager().changeSessionId(getSession().getId());
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
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
}
