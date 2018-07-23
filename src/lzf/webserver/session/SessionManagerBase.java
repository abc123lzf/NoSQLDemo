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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����10:16:33
* @Description �Ự���������
* ֮���Դ�������Ự�����������Ϊ���Ժ�����չHTTP��������Ĺ���
*/
public abstract class SessionManagerBase extends LifecycleBase {
	
	private static final Log log = LogFactory.getLog(SessionManagerBase.class);

	//Ĭ������������ʱ���������20����
	public static final int DEFAULT_MAX_INACTIVETIME = 20 * 60 * 1000;
	
	//����Session��Map��
	protected final Map<String, Session> sessions = new ConcurrentHashMap<>();
	
	//Ĭ�����ǻ�Ծ����ʱ��
	protected int defaultMaxSessionInactiveTime = DEFAULT_MAX_INACTIVETIME;
	
	//Session��������
	private final AtomicInteger sessionNum = new AtomicInteger(0);
	
	//��̨�̣߳����Session����
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
					//Ĭ��ÿ15��ִ��һ�μ��
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
	 * ��ȡĬ�ϵ�Session�ǻ�Ծ����ʱ��
	 * @return ������
	 */
	public final int getDefaultSessionMaxInactiveTime() {
		return defaultMaxSessionInactiveTime;
	}
	
	/**
	 * ����Session���ǻ�Ծ����ʱ��
	 * @param timeout ������
	 */
	public final void setSessionMaxInactiveTime(int timeout) {
		this.defaultMaxSessionInactiveTime = timeout;
	}
	
	/**
	 * ����һ���µ�Session����
	 * @return StrandardSessionʵ��
	 * @throws LifecycleException Session������δ��������״̬
	 */
	public final Session newSession() throws LifecycleException {
		checkLifecycleState();
		Session session = newSessionInternal();
		sessions.put(session.getId(), session);
		return session;
	}
	
	/**
	 * ����һ���µ�Session�����ʵ��ϸ��
	 */
	protected abstract Session newSessionInternal();
	
	/**
	 * ����Session Id��ȡSession����
	 * @param sessionId SessionId
	 * @param create true:���û���ҵ���Session�����򴴽�һ���µ�Session���󲢷���
	 * false:���û���ҵ��򷵻�null
	 * @return Sessionʵ��
	 * @throws LifecycleException Session������δ��������״̬
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
	 * �ȼ���getSession(sessionId, true);
	 * @throws LifecycleException Session������δ��������״̬
	 */
	public final Session getSession(String sessionId) throws LifecycleException {
		return getSession(sessionId, true);
	}
	
	/**
	 * �Ƴ�Session
	 * @param Session UUID
	 * @throws LifecycleException Session������δ��������״̬
	 */
	public final void removeSession(String sessionId) throws LifecycleException {
		checkLifecycleState();
		synchronized(sessions) {
			sessions.remove(sessionId);
		}
	}
	
	/**
	 * ����SessionID��UUIDֵ
	 * @param session ��Sessionʵ��
	 * @return �µ�UUIDֵ
	 * @throws LifecycleException Session������δ��������״̬
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
	 * @param sessionId ���޸ĵ�Sessionʵ����UUID
	 * @return ���ĺõ�SessionID��UUIDֵ
	 * @throws LifecycleException Session������δ��������״̬
	 */
	public final String changeSessionId(String sessionId) throws LifecycleException  {
		
		return changeSessionId(getSession(sessionId, false));
	}
	
	/**
	 * ��鵱ǰSession�������Ƿ�������״̬
	 * @throws LifecycleException Session������δ��������״̬
	 */
	protected final void checkLifecycleState() throws LifecycleException {
		
		if(!getLifecycleState().isAvailable())
			throw new LifecycleException("This Session Manager is not available");
	}
}
