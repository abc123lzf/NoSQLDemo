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
	private String id = UUID.randomUUID().toString().replaceAll("\\-", "");
	
	//����ʱ���
	private final long createTime = System.currentTimeMillis();
	
	//Ĭ�����ǻ�Ծ����ʱ��
	private int maxSessionInactiveTime;
	
	//������ʱ���
	private volatile long lastAccessTime = System.currentTimeMillis();
	
	//��Session�����Ƿ����½��ģ��ڶ��η��ʸ�Session������Ϊfalse
	private volatile boolean isNew = true;
	
	//�洢��Session����Attribute��Map
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	//��Session���������Ĺ�����
	private final HttpSessionManager manager;

	/**
	 * ��SessionManager������ö���
	 * @param manager SessionManagerʵ��
	 */
	public StandardSession(HttpSessionManager manager) {
		this.manager = manager;
		this.maxSessionInactiveTime = manager.getDefaultSessionMaxInactiveTime();
	}
	
	/**
	 * ��ȡ��Session�����е�����
	 * @param name ��������
	 * @return ���Զ������������򷵻�null
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * ��ȡ��Session�����������ĵ�����
	 * @return Enumeration������
	 */
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
		return maxSessionInactiveTime;
	}

	@Override
	public void updateLastAccessedTime() {
		isNew = false;
		lastAccessTime = System.currentTimeMillis();
	}
	
	/**
	 * ��ȡ��WebӦ�ö�Ӧ��ServletContext����
	 * @return ServletContextʵ��
	 */
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

	/**
	 * �жϸ�Session�����Ƿ����½��ģ��ڶ��η���Session����󷵻�ֵΪfalse
	 */
	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override @Deprecated
	public void putValue(String name, Object value) {
		attributeMap.put(name, value);
	}

	/**
	 * �Ƴ�Session��������
	 * @param name ������
	 */
	@Override
	public void removeAttribute(String name) {
		
		Object value = attributeMap.get(name);
		
		if(value != null) {
			attributeMap.remove(name);
			manager.getContext().getListenerContainer().runContextAttributeRemovedEvent(name, value);
		}
	}

	@Override @Deprecated
	public void removeValue(String name) {
		attributeMap.remove(name);
	}

	/**
	 * ����Session�����������
	 * @param name ������
	 * @param value ����ֵ
	 */
	@Override
	public void setAttribute(String name, Object value) {
		
		Object val = attributeMap.get(name);
		attributeMap.put(name, value);
		
		if(val == null) {	
			manager.getContext().getListenerContainer().runContextAttributeAddedEvent(name, value);
		} else {
			manager.getContext().getListenerContainer().runContextAttributeReplacedEvent(name, value);
		}
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public SessionManagerBase getSessionManager() {
		return manager;
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxSessionInactiveTime = maxInactiveInterval;
	}

	@Override
	public String changeId() {
		String newId = UUID.randomUUID().toString();
		this.id = newId;
		return newId;
	}

}