package lzf.webserver.connector;

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
import lzf.webserver.Host;
import lzf.webserver.Wrapper;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:45:14
* @Description HTTP�����࣬�����������з�װ
*/
public abstract class Request extends RequestBase {
	
	//��Cookie��URL��ȡ��sessionID(���������е�SessionID)
	private String sessionId;
	//����SessionID�Ǵ�Cookie��ȡ����
	private boolean sessionFromCookie = false;
	//����SessionID�Ǵ�URL��ȡ����
	private boolean sessionFromURL = false;
	
	//����Map
	protected final Map<String, Object> attributeMap = new ConcurrentHashMap<>();

	protected Host host = null;
	
	protected Context context = null;
	
	protected Wrapper wrapper = null;

	
	/**
	 * ��ȡ����ֵ
	 * @param ������
	 * @return ���Զ���
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * ��ȡ����Map�����������������ĵ�����
	 * @return Enumeration������
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
	 * ��������ֵ
	 * @param name ������
	 * @param obj ���Զ���
	 */
	@Override
	public void setAttribute(String name, Object obj) {
		attributeMap.put(name, obj);
	}

	/**
	 * ͨ���������Ƴ�����
	 * @param name ��Ҫ�Ƴ���������
	 */
	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ��ȡ��Ŀ����Ŀ¼(��������·��)
	 * �÷����ѷϳ�������ͨ��getServletContext().getRealPath(path)��ȡ
	 * @param path ����·��
	 * @return ��������·��
	 */
	@Override @Deprecated
	public String getRealPath(String path) {
		return getServletContext().getRealPath(path);
	}

	/**
	 * ��ȡ��������Ŀͻ��˵�������
	 */
	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ��ȡ��������Ŀͻ��˵�IP��ַ
	 * @return IP��ַ�ַ���
	 */
	@Override
	public String getLocalAddr() {
		return null;
	}

	/**
	 * ��ø�Web��������������Ķ˿�
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
	 * �����������Servlet����ʵ����·��
	 * �������Servlet�������򷵻�null
	 */
	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * ��HTTP�����л��SessionIDֵ
	 * ͨ��Cookie��URL�����л�ȡSessionID
	 */
	@Override
	public String getRequestedSessionId() {
		Cookie[] cookies = getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(context.getSessionIdName())) {
				
				this.sessionFromCookie = true;
				return cookie.getName();
			}
		}
		String sessionId = super.getParameter(context.getSessionIdName());
		if(sessionId != null) {
			this.sessionFromURL = true;
			return sessionId;
		}
		return null;
	}

	/**
	 * ��ȡSession����
	 * @param create true:���û���ҵ��򴴽�һ���µ�Session false:���û���ҵ��򷵻�null
	 */
	@Override
	public HttpSession getSession(boolean create) {
		if(create)
			return getSession();
		//��URL��Cookie�в���SessionID
		if(this.sessionId == null)
			this.sessionId = getRequestedSessionId();
		//���û���ҵ��򷵻�null
		if(this.sessionId == null)
			return null;
		//����Ӵ�URL��Cookie���ҵ�SessionID���Session���������Ҹ�Session����
		return context.getSessionManager().getHttpSession(sessionId, false);
	}

	/**
	 * ��ȡSession�������û���ҵ��򴴽�һ���µ�Session
	 * ��ͬ��getSession(true)
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

	/**
	 * �жϴ�URL��Cookie�е���ȡ�ĻỰID���������Ƿ����
	 * @return �Ự��������
	 */
	@Override
	public boolean isRequestedSessionIdValid() {
		if(this.sessionId == null)
			getRequestedSessionId();
		if(context.getSessionManager().getSession(sessionId, false) == null)
			return true;
		return false;
	}

	/**
	 * �жϴ�URL��Cookie�е���ȡ�ĻỰID����Cooike��
	 * @return ����Cookie��
	 */
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		if(this.sessionId == null)
			getRequestedSessionId();
		return sessionFromCookie;
	}

	/**
	 * �жϴ�URL��Cookie�е���ȡ�ĻỰID����URL��
	 * @return ����URL��
	 */
	@Override
	public boolean isRequestedSessionIdFromURL() {
		if(this.sessionId == null)
			getRequestedSessionId();
		return sessionFromURL;
	}

	/**
	 * �жϴ�URL��Cookie�е���ȡ�ĻỰID����Cooike��
	 * @return ����URL��
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
	
	public Host getHost() {
		return this.host;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public Wrapper getWrapper() {
		return this.wrapper;
	}
}