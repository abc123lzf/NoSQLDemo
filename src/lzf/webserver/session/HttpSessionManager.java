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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����4:20:06
* @Description �Ự�����࣬ÿ��WebӦ�ö�Ӧһ���Ự������a
*/
public final class HttpSessionManager extends SessionManagerBase {
	
	private static final Log log = LogFactory.getLog(HttpSessionManager.class);
	
	//WebApp����ʵ��
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
	 * ����Session Id��ȡHttpSession����
	 * @param sessionId SessionId
	 * @param create true:���û���ҵ���Session�����򴴽�һ���µ�Session���󲢷���
	 * false:���û���ҵ��򷵻�null
	 * @return HttpSessionʵ��
	 */
	public HttpSession getHttpSession(String sessionId, boolean create) {
		return (HttpSession)getSession(sessionId, true);
	}
	
	/**
	 * ��ȡ��SessionManager������Context����
	 * @return Contextʵ��
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * ����һ����׼HTTP�Ự��
	 * @see SessionManagerBase.newSession();
	 * @return ��SessionId
	 */
	@Override
	protected Session newSessionInternal() {
		StandardSession session = new StandardSession(this);
		return session;
	}
	
}
