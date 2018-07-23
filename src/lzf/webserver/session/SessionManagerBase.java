package lzf.webserver.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Session;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 上午10:16:33
* @Description 会话抽象管理器
* 之所以创建这个会话管理抽象类是为了以后能扩展HTTP服务以外的功能
*/
public abstract class SessionManagerBase extends LifecycleBase {
	
	private static final Log log = LogFactory.getLog(SessionManagerBase.class);

	//默认最大空闲生存时间毫秒数：20分钟
	public static final int DEFAULT_MAX_INACTIVETIME = 20 * 60 * 1000;
	
	//保存Session的Map，
	protected final Map<String, Session> sessions = new ConcurrentHashMap<>();
	
	//默认最大非活跃生存时间
	protected int defaultMaxSessionInactiveTime = DEFAULT_MAX_INACTIVETIME;
	
	//Session对象总数
	private final AtomicInteger sessionNum = new AtomicInteger(0);
	
	//后台线程，监测Session对象
	protected final LifeCheckProcesser processer = new LifeCheckProcesser();
	
	protected class LifeCheckProcesser implements Runnable {
		@Override
		public void run() {
			log.info("LifeCheckProcesser run");
			
			while(true) {
				
				if(getLifecycleState().after(LifecycleState.STOPPING_PREP))
					break;
				
				long nowTime = System.currentTimeMillis();
				
				for(Map.Entry<String, Session> entry : sessions.entrySet()) {
					Session session = entry.getValue();
					if(nowTime - session.getLastAccessedTime() >= session.getMaxInactiveInterval()) {
						sessions.remove(entry.getKey());
						sessionNum.decrementAndGet();
					}
				}
				try {
					//默认每15秒执行一次检查
					Thread.sleep(15 * 1000);
				} catch (InterruptedException e) {
					log.error(e);
					new Thread(processer).start();
					break;
				}
			}
		}
	}
	
	/**
	 * 获取默认的Session非活跃过期时间
	 * @return 毫秒数
	 */
	public final int getDefaultSessionMaxInactiveTime() {
		return defaultMaxSessionInactiveTime;
	}
	
	/**
	 * 设置Session最大非活跃生存时间
	 * @param timeout 毫秒数
	 */
	public final void setSessionMaxInactiveTime(int timeout) {
		this.defaultMaxSessionInactiveTime = timeout;
	}
	
	/**
	 * 创建一个新的Session对象
	 * @return StrandardSession实例
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final Session newSession() throws LifecycleException {
		checkLifecycleState();
		Session session = newSessionInternal();
		sessions.put(session.getId(), session);
		return session;
	}
	
	/**
	 * 创建一个新的Session对象的实现细节
	 */
	protected abstract Session newSessionInternal();
	
	/**
	 * 根据Session Id获取Session对象
	 * @param sessionId SessionId
	 * @param create true:如果没有找到该Session对象则创建一个新的Session对象并返回
	 * false:如果没有找到则返回null
	 * @return Session实例
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final Session getSession(String sessionId, boolean create) throws LifecycleException {
		
		if(sessionId == null)
			return newSession();
		
		Session session = sessions.get(sessionId);
		if(session != null) {
			session.updateLastAccessedTime();
			return session;
		}
		
		if(create)
			return newSession();
		return null;
	}
	
	/**
	 * 等价于getSession(sessionId, true);
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final Session getSession(String sessionId) throws LifecycleException {
		return getSession(sessionId, true);
	}
	
	/**
	 * 移除Session
	 * @param Session UUID
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final void removeSession(String sessionId) throws LifecycleException {
		checkLifecycleState();
		synchronized(sessions) {
			sessions.remove(sessionId);
		}
	}
	
	/**
	 * 更改SessionID的UUID值
	 * @param session 该Session实例
	 * @return 新的UUID值
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final String changeSessionId(Session session) throws LifecycleException {
		
		checkLifecycleState();
		
		if(session == null)
			return null;
		Session s = sessions.get(session.getId());
		if(s == null)
			return null;
		synchronized(sessions) {
			sessions.remove(session.getId());
			s.updateLastAccessedTime();
			s.changeId();
			sessions.put(s.getId(), s);
		}
		return s.getId();
	}
	
	/**
	 * @param sessionId 待修改的Session实例的UUID
	 * @return 更改好的SessionID的UUID值
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	public final String changeSessionId(String sessionId) throws LifecycleException  {
		
		return changeSessionId(getSession(sessionId, false));
	}
	
	/**
	 * 检查当前Session管理器是否处于启动状态
	 * @throws LifecycleException Session管理器未处于启动状态
	 */
	protected final void checkLifecycleState() throws LifecycleException {
		
		if(!getLifecycleState().isAvailable())
			throw new LifecycleException("This Session Manager is not available");
	}
}
