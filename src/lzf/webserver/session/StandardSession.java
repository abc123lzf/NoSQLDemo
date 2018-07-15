package lzf.webserver.session;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import lzf.webserver.Session;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����3:45:14
* @Description ��׼�Ự�ࡢ��ʱ��֧��WebSocketģʽ
*/
@SuppressWarnings("deprecation")
public class StandardSession implements Session, HttpSession, Serializable {

	private static final long serialVersionUID = 8941015350279589423L;
	
	//Session Id
	private final String id = UUID.randomUUID().toString();
	
	//����ʱ���
	private final long createTime = System.currentTimeMillis();
	
	//������ʱ���
	private volatile long lastAccessTime = System.currentTimeMillis();
	
	//��Session�����Ƿ����½��ģ��ڶ��η��ʸ�Session������Ϊfalse
	private volatile boolean isNew = true;
	
	//�洢��Session����Attribute��Map
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	//��Session���������Ĺ�����
	private final SessionManager manager;

	/**
	 * ��SessionManager������ö���
	 * @param manager SessionManagerʵ��
	 */
	public StandardSession(SessionManager manager) {
		this.manager = manager;
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
	public long getCreationTime() {
		return createTime;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return manager.getSessionMaxInactiveTime();
	}

	@Override
	public void updateLastAccessedTime() {
		isNew = false;
		lastAccessTime = System.currentTimeMillis();
	}
	
	@Override
	public ServletContext getServletContext() {
		return manager.getContext().getServletContext();
	}

	@Override @Deprecated
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
	public String getId() {
		return id;
	}

	@Override
	public SessionManager getSessionManager() {
		return manager;
	}

}