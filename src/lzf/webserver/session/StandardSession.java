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
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午3:45:14
* @Description 标准会话类、暂时不支持WebSocket模式
*/
@SuppressWarnings("deprecation")
public class StandardSession implements Session, HttpSession, Serializable {

	private static final long serialVersionUID = 8941015350279589423L;
	
	//Session Id
	private String id = UUID.randomUUID().toString().replaceAll("\\-", "");
	
	//创建时间戳
	private final long createTime = System.currentTimeMillis();
	
	//默认最大非活跃生存时间
	private int maxSessionInactiveTime;
	
	//最后访问时间戳
	private volatile long lastAccessTime = System.currentTimeMillis();
	
	//该Session对象是否是新建的，第二次访问该Session对象会改为false
	private volatile boolean isNew = true;
	
	//存储该Session对象Attribute的Map
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	//该Session对象所属的管理器
	private final HttpSessionManager manager;

	/**
	 * 由SessionManager负责构造该对象
	 * @param manager SessionManager实例
	 */
	public StandardSession(HttpSessionManager manager) {
		this.manager = manager;
		this.maxSessionInactiveTime = manager.getDefaultSessionMaxInactiveTime();
	}
	
	/**
	 * 获取该Session对象中的属性
	 * @param name 属性名称
	 * @return 属性对象，若不存在则返回null
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * 获取该Session对象属性名的迭代器
	 * @return Enumeration迭代器
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
	 * 获取该Web应用对应的ServletContext对象
	 * @return ServletContext实例
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
	 * 判断该Session对象是否是新建的，第二次访问Session对象后返回值为false
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
	 * 移除Session对象属性
	 * @param name 属性名
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
	 * 往该Session对象添加属性
	 * @param name 属性名
	 * @param value 属性值
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