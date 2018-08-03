package lzf.webserver.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 下午9:14:53
* @Description Request外观类，供Servlet使用，以达到屏蔽内部Request相关方法
*/
public class RequestFacade implements HttpServletRequest {
	
	private final Request request;
	
	public RequestFacade(Request request) {
		this.request = request;
	}

	@Override
	public Object getAttribute(String name) {
		//System.out.println("getAttribute[" + name + "]");
		return request.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		//System.out.println("getAttributeNames");
		return request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		//System.out.println("getCharacterEncoding");
		return request.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		//System.out.println("setCharacterEncoding + [" + env + "]");
		request.setCharacterEncoding(env);
	}

	@Override
	public int getContentLength() {
		//System.out.println("getContentLength");
		return request.getContentLength();
	}

	@Override
	public long getContentLengthLong() {
		//System.out.println("getContentLengthLong");
		return request.getContentLengthLong();
	}

	@Override
	public String getContentType() {
		//System.out.println("getContentType");
		return request.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		//System.out.println("getInputStream");
		return request.getInputStream();
	}

	@Override
	public String getParameter(String name) {
		//System.out.println("getParameter [" + name + "]");
		return request.getParameter(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		//System.out.println("getParameterNames");
		return request.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		//System.out.println("getParameterValues [" + name + "]");
		return request.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		//System.out.println("getParameterMap");
		return request.getParameterMap();
	}

	@Override
	public String getProtocol() {
		//System.out.println("getProtocol");
		return request.getProtocol();
	}

	@Override
	public String getScheme() {
		//System.out.println("getScheme");
		return request.getScheme();
	}

	@Override
	public String getServerName() {
		//System.out.println("getServerName");
		return request.getServerName();
	}

	@Override
	public int getServerPort() {
		//System.out.println("getServerPort");
		return request.getServerPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		//System.out.println("getReader");
		return request.getReader();
	}

	@Override
	public String getRemoteAddr() {
		//System.out.println("getRemoteAddr");
		return request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		//System.out.println("getRemoteHost");
		return request.getRemoteHost();
	}

	@Override
	public void setAttribute(String name, Object o) {
		//System.out.println("setAttribute [ name=" + name + ", object=" + o + "]");
		request.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		//System.out.println("removeAttribute [" + name + "]");
		request.removeAttribute(name);
	}

	@Override
	public Locale getLocale() {
		//System.out.println("getLocale");
		return request.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		//System.out.println("getLocales");
		return request.getLocales();
	}

	@Override
	public boolean isSecure() {
		//System.out.println("isSecure");
		return request.isSecure();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		//System.out.println("getRequestDispatcher [" + path + "]");
		return request.getRequestDispatcher(path);
	}

	@Override @Deprecated
	public String getRealPath(String path) {
		///System.out.println("getRealPath [" + path + "]");
		return request.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		//System.out.println("getRemotePort");
		return request.getRemotePort();
	}

	@Override
	public String getLocalName() {
		//System.out.println("getLocalName");
		return request.getLocalName();
	}

	@Override
	public String getLocalAddr() {
		//System.out.println("getLocalAddr");
		return request.getLocalAddr();
	}

	@Override
	public int getLocalPort() {
		//System.out.println("getLocalPort");
		return request.getLocalPort();
	}

	@Override
	public ServletContext getServletContext() {
		//System.out.println("getServletContext");
		return request.getServletContext();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		//System.out.println("startAsync");
		return request.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		//System.out.println("startAsync()");
		return request.startAsync(servletRequest, servletResponse);
	}

	@Override
	public boolean isAsyncStarted() {
		//System.out.println("isAsyncStarted");
		return request.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		//System.out.println("isAsyncSupported");
		return request.isAsyncStarted();
	}

	@Override
	public AsyncContext getAsyncContext() {
		//System.out.println("getAsyncContext");
		return request.getAsyncContext();
	}

	@Override
	public DispatcherType getDispatcherType() {
		//System.out.println("getDispatcherType");
		return request.getDispatcherType();
	}

	@Override
	public String getAuthType() {
		//System.out.println("getAuthType");
		return request.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		//System.out.println("getCookies");
		return request.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		//System.out.println("getDateHeader [" + name + "]");
		return request.getDateHeader(name);
	}

	@Override
	public String getHeader(String name) {
		//System.out.println("getHeader [" + name + "]");
		return request.getHeader(name);
	}

	@Override @Deprecated
	public Enumeration<String> getHeaders(String name) {
		//System.out.println("getHeaders [" + name + "]");
		return request.getHeaders(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		//System.out.println("getHeaderNames");
		return request.getHeaderNames();
	}

	@Override
	public int getIntHeader(String name) {
		//System.out.println("getIntHeader [" + name + "]");
		return request.getIntHeader(name);
	}

	@Override
	public String getMethod() {
		//System.out.println("getMethod");
		return request.getMethod();
	}

	@Override
	public String getPathInfo() {
		//System.out.println("getPathInfo");
		return request.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		//System.out.println("getPathTranslated");
		return request.getPathTranslated();
	}

	@Override
	public String getContextPath() {
		//System.out.println("getContextPath");
		return request.getContextPath();
	}

	@Override
	public String getQueryString() {
		//System.out.println("getQueryString");
		return request.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		//System.out.println("getRemoteUser");
		return request.getRemoteUser();
	}

	@Override
	public boolean isUserInRole(String role) {
		return request.isUserInRole(role);
	}

	@Override
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	@Override
	public String getRequestedSessionId() {
		//System.out.println("getRequestedSessionId");
		return request.getRequestedSessionId();
	}

	@Override
	public String getRequestURI() {
		//System.out.println("getRequestURI");
		return request.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		//System.out.println("getRequestURL");
		return request.getRequestURL();
	}

	@Override
	public String getServletPath() {
		//System.out.println("getServletPath");
		return request.getServletPath();
	}

	@Override
	public HttpSession getSession(boolean create) {
		//System.out.println("getSession [" + create + "]");
		return request.getSession(create);
	}

	@Override
	public HttpSession getSession() {
		//System.out.println("getSession");
		return request.getSession();
	}

	@Override
	public String changeSessionId() {
		//System.out.println("changeSessionId");
		return request.changeSessionId();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		//System.out.println("isRequestedSessionIdValid");
		return request.isRequestedSessionIdValid();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		//System.out.println("isRequestedSessionIdFromCookie");
		return request.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		//System.out.println("isRequestedSessionIdFromURL");
		return request.isRequestedSessionIdFromURL();
	}

	@Override @Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		//System.out.println("isRequestedSessionIdFromUrl");
		return request.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		return request.authenticate(response);
	}

	@Override
	public void login(String username, String password) throws ServletException {
		//System.out.println("login");
		request.login(username, password);
	}

	@Override
	public void logout() throws ServletException {
		//System.out.println("logout");
		request.logout();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		//System.out.println("getParts");
		return request.getParts();
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		//System.out.println("getPart");
		return request.getPart(name);
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass)
			throws IOException, ServletException {
		return request.upgrade(httpUpgradeHandlerClass);
	}
}
