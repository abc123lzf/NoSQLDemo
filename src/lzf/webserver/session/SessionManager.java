package lzf.webserver.session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import lzf.webserver.Context;
import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleListener;
import lzf.webserver.LifecycleState;
import lzf.webserver.Session;
import lzf.webserver.core.LifecycleSupport;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午4:20:06
* @Description 会话管理类，每个Web应用对应一个会话管理类a
*/
public final class SessionManager implements Lifecycle {
	
	//默认最大空闲生存时间毫秒数：20分钟
	public static final int DEFAULT_MAX_INACTIVETIME = 20 * 60 * 1000;
	
	//WebApp容器实例
	private final Context context;
	//保存Session的Map，
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();
	//默认最大非活跃生存时间
	private int defaultMaxSessionInactiveTime = DEFAULT_MAX_INACTIVETIME;
	//该web应用Session对象总数
	private final AtomicInteger sessionNum = new AtomicInteger(0);
	//后台线程，监测Session对象
	private final LifeCheckProcesser processer = new LifeCheckProcesser();
	
	private final LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
	
	private volatile LifecycleState state = LifecycleState.NEW;
	
	/**
	 * SessionManager后台线程，负责监测Session对象生存周期
	 */
	private class LifeCheckProcesser implements Runnable {
		@Override
		public void run() {
			while(true) {
				if(state.after(LifecycleState.STOPPING_PREP))
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
					e.printStackTrace();
					new Thread(processer).start();
					break;
				}
			}
		}
	}
	
	public SessionManager(Context context) {
		this.context = context;
	}
	
	public SessionManager(Context context, int defaultMaxSessionInactiveTime) {
		this(context);
		this.defaultMaxSessionInactiveTime = defaultMaxSessionInactiveTime;
	}
	
	/**
	 * 根据Session Id获取HttpSession对象
	 * @param sessionId SessionId
	 * @param create true:如果没有找到该Session对象则创建一个新的Session对象并返回
	 * false:如果没有找到则返回null
	 * @return HttpSession实例
	 */
	public HttpSession getSession(String sessionId, boolean create) {
		Session session = sessions.get(sessionId);
		if(session != null) {
			session.updateLastAccessedTime();
			return (HttpSession)session;
		}
		if(create)
			return (HttpSession)createSession();
		return null;
	}
	
	/**
	 * 创建一个新的Session对象
	 * @return 该SessionId
	 */
	public HttpSession newSession() {
		return (HttpSession) createSession();
	}
	
	/**
	 * 创建一个新的Session对象
	 * @return StrandardSession实例
	 */
	private Session createSession() {
		StandardSession session = new StandardSession(this);
		sessions.put(session.getId(), session);
		return session;
	}
	
	/**
	 * 更改SessionID的UUID值
	 * @param session 该Session实例
	 * @return 新的UUID值
	 */
	public String changeSessionId(HttpSession session) {
		if(session == null)
			return null;
		
		StandardSession stdSession = (StandardSession) sessions.get(session.getId());
		if(stdSession == null)
			return null;
		
		synchronized(sessions) {
			sessions.remove(session.getId());
			stdSession.updateLastAccessedTime();
			stdSession.changeId();
			sessions.put(stdSession.getId(), stdSession);
		}
		return stdSession.getId();
	}
	
	/**
	 * 获取该SessionManager所属的Context容器
	 * @return Context实例
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * 获取默认的Session非活跃过期时间
	 * @return 毫秒数
	 */
	public int getDefaultSessionMaxInactiveTime() {
		return defaultMaxSessionInactiveTime;
	}
	
	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycleSupport.addLifecycleListener(listener);
	}


	@Override
	public List<LifecycleListener> getLifecycleListeners() {
		return lifecycleSupport.getLifecycleListeners();
	}


	@Override
	public void init() throws LifecycleException {
		if(state.after(LifecycleState.INITIALIZING))
			throw new LifecycleException();
		
		state = LifecycleState.INITIALIZING;
		lifecycleSupport.runLifecycleEvent(null);
		state = LifecycleState.INITIALIZED;
		lifecycleSupport.runLifecycleEvent(null);
	}
	
	@Override
	public void start() throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException();
		
		state = LifecycleState.STARTING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		new Thread(processer).start();
		
		state = LifecycleState.STARTED;
		lifecycleSupport.runLifecycleEvent(null);
	}


	@Override
	public void stop() throws LifecycleException {
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException();
		
		state = LifecycleState.STOPPING_PREP;
		lifecycleSupport.runLifecycleEvent(null);
		
		state = LifecycleState.STOPPING;
		//TODO 持久化所有的session
		
		state = LifecycleState.STOPPED;
		lifecycleSupport.runLifecycleEvent(null);
	}


	@Override
	public void destory() throws LifecycleException {
		
	}


	@Override
	public LifecycleState getLifecycleState() {
		return state;
	}
}
