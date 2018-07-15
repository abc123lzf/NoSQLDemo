package lzf.webserver.session;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.MessageHandler.Partial;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午3:45:14
* @Description 标准会话类、暂时不支持WebSocket模式
*/
public class StandardSession implements Session, HttpSession, Serializable {

	private static final long serialVersionUID = 8941015350279589423L;
	
	private String id = UUID.randomUUID().toString();
	
	private final long createTime = System.currentTimeMillis();
	private volatile long lastAccessTime = System.currentTimeMillis();
	private int maxInactiveTime;
	
	private volatile boolean isNew = true;
	
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	private final SessionManager manager;

	public StandardSession(SessionManager manager) {
		this.manager = manager;
	}
	
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCreationTime() {
		return createTime;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveTime;
	}

	@Override
	public ServletContext getServletContext() {
		return manager.getContext().getServletContext();
	}

	@Override @SuppressWarnings("deprecation")
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException();
	}

	@Override @Deprecated
	public Object getValue(String name) {
		return attributeMap.get(name);
	}

	@Override @Deprecated
	public String[] getValueNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void invalidate() {
		attributeMap.clear();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override @Deprecated
	public void putValue(String name, Object value) {
		attributeMap.put(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	@Override @Deprecated
	public void removeValue(String name) {
		attributeMap.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		attributeMap.put(name, value);
	}

	@Override
	public void setMaxInactiveInterval(int time) {
		this.maxInactiveTime = time;
	}

	@Override
	public void addMessageHandler(MessageHandler arg0) throws IllegalStateException {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public <T> void addMessageHandler(Class<T> arg0, Partial<T> arg1) throws IllegalStateException {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public <T> void addMessageHandler(Class<T> arg0, Whole<T> arg1) throws IllegalStateException {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void close(CloseReason arg0) throws IOException {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Async getAsyncRemote() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Basic getBasicRemote() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public WebSocketContainer getContainer() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getMaxBinaryMessageBufferSize() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public long getMaxIdleTimeout() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public int getMaxTextMessageBufferSize() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Set<MessageHandler> getMessageHandlers() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public List<Extension> getNegotiatedExtensions() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public String getNegotiatedSubprotocol() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Set<Session> getOpenSessions() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Map<String, String> getPathParameters() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public String getProtocolVersion() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public String getQueryString() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Map<String, List<String>> getRequestParameterMap() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public URI getRequestURI() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public Map<String, Object> getUserProperties() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void removeMessageHandler(MessageHandler arg0) {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void setMaxBinaryMessageBufferSize(int arg0) {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void setMaxIdleTimeout(long arg0) {
		throw new UnsupportedOperationException("WebSocket not support");
	}

	@Override
	public void setMaxTextMessageBufferSize(int arg0) {
		throw new UnsupportedOperationException("WebSocket not support");
	}

}
