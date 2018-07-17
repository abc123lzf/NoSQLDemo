package lzf.webserver.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import lzf.webserver.Context;
import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleBase;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.Session;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����4:20:06
* @Description �Ự�����࣬ÿ��WebӦ�ö�Ӧһ���Ự������a
*/
public final class SessionManager extends LifecycleBase implements Lifecycle {
	
	//Ĭ������������ʱ���������20����
	public static final int DEFAULT_MAX_INACTIVETIME = 20 * 60 * 1000;
	
	//WebApp����ʵ��
	private final Context context;
	//����Session��Map��
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();
	//Ĭ�����ǻ�Ծ����ʱ��
	private int defaultMaxSessionInactiveTime = DEFAULT_MAX_INACTIVETIME;
	//��webӦ��Session��������
	private final AtomicInteger sessionNum = new AtomicInteger(0);
	//��̨�̣߳����Session����
	private final LifeCheckProcesser processer = new LifeCheckProcesser();
	
	/**
	 * SessionManager��̨�̣߳�������Session������������
	 */
	private class LifeCheckProcesser implements Runnable {
		@Override
		public void run() {
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
					//Ĭ��ÿ15��ִ��һ�μ��
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
	 * ����һ���µ�Session����
	 * @return ��SessionId
	 */
	public HttpSession newSession() {
		return (HttpSession) createSession();
	}
	
	/**
	 * ����һ���µ�Session����
	 * @return StrandardSessionʵ��
	 */
	private Session createSession() {
		StandardSession session = new StandardSession(this);
		sessions.put(session.getId(), session);
		return session;
	}
	
	/**
	 * ����SessionID��UUIDֵ
	 * @param session ��Sessionʵ��
	 * @return �µ�UUIDֵ
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
	 * ��ȡ��SessionManager������Context����
	 * @return Contextʵ��
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * ��ȡĬ�ϵ�Session�ǻ�Ծ����ʱ��
	 * @return ������
	 */
	public int getDefaultSessionMaxInactiveTime() {
		return defaultMaxSessionInactiveTime;
	}
}
