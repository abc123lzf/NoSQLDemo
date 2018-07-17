package lzf.webserver.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Session;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午4:20:06
* @Description 会话管理类，每个Web应用对应一个会话管理类a
*/
public final class HttpSessionManager extends SessionManagerBase {
	
	private static final Log log = LogFactory.getLog(HttpSessionManager.class);
	
	//WebApp容器实例
	private final Context context;
	
	public HttpSessionManager(Context context) {
		this.context = context;
	}
	
	public HttpSessionManager(Context context, int defaultMaxSessionInactiveTime) {
		this(context);
		this.defaultMaxSessionInactiveTime = defaultMaxSessionInactiveTime;
	}
	
	@Override
	protected void initInternal() throws LifecycleException {
		
	}

	@Override
	protected void startInternal() throws LifecycleException {
		new Thread(processer).start();
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		
	}

	@Override
	protected void destoryInternal() throws LifecycleException {

	}
	
	/**
	 * 根据Session Id获取HttpSession对象
	 * @param sessionId SessionId
	 * @param create true:如果没有找到该Session对象则创建一个新的Session对象并返回
	 * false:如果没有找到则返回null
	 * @return HttpSession实例
	 */
	public HttpSession getHttpSession(String sessionId, boolean create) {
		return (HttpSession)getSession(sessionId, true);
	}
	
	/**
	 * 获取该SessionManager所属的Context容器
	 * @return Context实例
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * 创建一个标准HTTP会话类
	 * @see SessionManagerBase.newSession();
	 * @return 该SessionId
	 */
	@Override
	protected Session newSessionInternal() {
		StandardSession session = new StandardSession(this);
		return session;
	}
	
}
