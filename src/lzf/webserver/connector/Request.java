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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:45:14
* @Description HTTP�����࣬�����������з�װ
*/
public abstract class Request extends RequestBase {
	
	public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy",Locale.US);
	
	Response response;
	
	private static final Log log = LogFactory.getLog(Request.class);
	
	private int localPort = 80;
	
	private String localName = null;
	
	private String localAddr = null;
	
	//��Cookie��URL��ȡ��sessionID(���������е�SessionID)
	private String sessionId;
	
	//����SessionID�Ǵ�Cookie��ȡ����
	private boolean sessionFromCookie = false;
	
	//����SessionID�Ǵ�URL��ȡ����
	private boolean sessionFromURL = false;
	
	//����Map
	protected final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	//��Request��·�ɵ���Host����
	protected Host host = null;
	
	//��Request��·�ɵ���Context����
	protected Context context = null;
	
	//��Wrapper��·�ɵ���Wrapper����
	protected Wrapper wrapper = null;

	//Session�������е�HttpSessionʵ��
	private HttpSession session = null;
	
	private DispatcherType dispatcherType = DispatcherType.REQUEST;
	
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
		
		Object value = attributeMap.get(name);
		attributeMap.put(name, obj);
		
		if(value == null) {
			context.getListenerContainer().runRequestAttributeAddedEvent(this, name, value);
		} else {
			context.getListenerContainer().runRequestAttributeReplacedEvent(this, name, value);
		}
	}

	/**
	 * ͨ���������Ƴ�����
	 * @param name ��Ҫ�Ƴ���������
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
		return localName;
	}

	/**
	 * ��ȡ��������Ŀͻ��˵�IP��ַ
	 * @return IP��ַ�ַ���
	 */
	@Override
	public String getLocalAddr() {
		return localAddr;
	}

	/**
	 * ��ø�Web��������������Ķ˿�
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
	 * ��ȡ�������ת���ͣ�Ĭ��ΪRequest��������getRequestDispatcher��forward������include����ʱ�����޸ĸ�
	 * �����ֵ
	 * @return �������ת���Ͷ���
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
			log.error("ʱ��ת���쳣", e);
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
		//�ȴ�Cookie�в���Session
		Cookie[] cookies = getCookies();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(context.getSessionIdName())) {
				this.sessionFromCookie = true;
				return cookie.getValue();
			}
		}
		//�ٴ�URI������Ѱ��
		String sessionId = super.getParameter(context.getSessionIdName());
		if(sessionId != null) {
			this.sessionFromURL = true;
			return sessionId;
		}
		//�����û���ҵ��򷵻�null
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
		
		//�����URL��Cookie���ҵ�SessionID���Session���������Ҹ�Session���󣬸÷���ֵ����Ϊnull��
		//����û��ǵ�һ�η���ҳ����߻Ự�ѹ���
		try {
			session = context.getSessionManager().getHttpSession(sessionId, false);
		} catch (LifecycleException e) {
			log.error("Session������������", e);
		}
		return session;
	}

	/**
	 * ���������е�Session�ֶλ�ȡSession�������û���ҵ��򴴽�һ���µ�Session
	 * ��ͬ��getSession(true)
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
			log.error("Session������������", e);
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
			log.error("Session������������", e);
			return null;
		}
	}

	/**
	 * �жϴ�URL��Cookie�е���ȡ�ĻỰID���������Ƿ����
	 * @return �Ự��������
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
			log.error("Session������������", e);
			return true;
		}
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
	
	/**
	 * @return ��Request��·�ɵ���Host����
	 */
	public Host getHost() {
		return this.host;
	}
	
	/**
	 * @return ��Request��·�ɵ���Context����
	 */
	public Context getContext() {
		return this.context;
	}
	
	/**
	 * @return ��Request��·�ɵ���Wrapper����
	 */
	public Wrapper getWrapper() {
		return this.wrapper;
	}
	
	public void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	/**
	 * ��������URI�������������forward��תʹ��
	 * @param uri URI·������������
	 */
	public void setRequestURI(String uri) {
		this.requestUrl = uri;
	}
	
	/**
	 * ���URI�����Ĳ������Ա�forward��ת
	 */
	public void cleanParameterMap() {
		parameterMap.clear();
	}

	/**
	 * @return ��֮�����Response����
	 */
	public Response getResponse() {
		return response;
	}

}