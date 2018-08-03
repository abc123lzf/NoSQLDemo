package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.util.res.StringManager;

import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleEvent;
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleListener;
import lzf.webserver.LifecycleState;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��16�� ����7:18:34
* @Description ��˵��
*/
public abstract class LifecycleBase implements Lifecycle {
	
	private static final StringManager sm = StringManager.getManager(LifecycleBase.class);

	private final Log log = LogFactory.getLog(LifecycleBase.class);
	
	private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();
	
	private volatile LifecycleState state = LifecycleState.NEW;

	/**
	 * �����ʼ��
	 */
	@Override
	public final synchronized void init() throws Exception {
		
		if(state.after(LifecycleState.INITIALIZING))
			throw new LifecycleException(sm.getString("LifecycleBase.init.e0"));
		
		if(log.isDebugEnabled())
			log.debug(this.getClass().getName() + " init");
		
		state = LifecycleState.INITIALIZING;
		runLifecycleEvent(null);
		
		initInternal();
		if(state == LifecycleState.FAILED)
			throw new LifecycleException();
		
		state = LifecycleState.INITIALIZED;
		runLifecycleEvent(null);
	}

	/**
	 * �������
	 */
	@Override
	public final synchronized void start() throws Exception {
		
		if(state.after(LifecycleState.STARTING_PREP))
			throw new LifecycleException(sm.getString("LifecycleBase.start.e0"));
		
		if(log.isDebugEnabled())
			log.debug(this.getClass().getName() + " start");
		
		state = LifecycleState.STARTING_PREP;
		runLifecycleEvent(null);
		
		state = LifecycleState.STARTING;
		startInternal();
		if(state == LifecycleState.FAILED)
			throw new LifecycleException();
		
		state = LifecycleState.STARTED;
		runLifecycleEvent(null);
	}

	/**
	 * ���ֹͣ
	 */
	@Override
	public final synchronized void stop() throws Exception {
		
		if(state.after(LifecycleState.STOPPING_PREP) || state.before(LifecycleState.INITIALIZED))
			throw new LifecycleException(sm.getString("LifecycleBase.stop.e0"));
		
		log.info(this.getClass().getName() + " stop");
		
		state = LifecycleState.STOPPING_PREP;
		runLifecycleEvent(null);
		
		state = LifecycleState.STOPPING;
		stopInternal();
		
		if(state == LifecycleState.FAILED)
			throw new LifecycleException();
		
		state = LifecycleState.STOPPED;
		runLifecycleEvent(null);
	}

	/**
	 * ���ֹͣ
	 */
	@Override
	public final synchronized void destory() throws Exception {
		
		if(state.after(LifecycleState.DESTORYED))
			throw new LifecycleException(sm.getString("LifecycleBase.destory.e0"));
		
		state = LifecycleState.DESTORYING;
		runLifecycleEvent(null);
		
		destoryInternal();
		
		if(state == LifecycleState.FAILED)
			throw new LifecycleException();
		
		state = LifecycleState.DESTORYED;
		runLifecycleEvent(null);
	}
	
	/**
	 * ��ʼ�����ϸ��
	 * @throws LifecycleException �����ڳ�ʼ������ø÷���
	 */
	protected abstract void initInternal() throws Exception;
	
	/**
	 * �������ϸ��
	 * @throws LifecycleException ������δ����ʼ����������֮����ø÷���
	 */
	protected abstract void startInternal() throws Exception;
	
	/**
	 * ֹͣ���ϸ��
	 * @throws LifecycleException ������δ����ʼ��ǰ��������ٺ���ø÷���
	 */
	protected abstract void stopInternal() throws Exception;
	
	/**
	 * ֹͣ���ϸ��
	 * @throws LifecycleException ������������ٺ���ø÷���
	 */
	protected abstract void destoryInternal() throws Exception;

	/**
	 * ��ȡ��������״̬
	 * @return LifecycleState״̬����
	 */
	@Override
	public final LifecycleState getLifecycleState() {
		return state;
	}
	
	protected final void setLifecycleState(LifecycleState state) {
		this.state = state;
	}
	
	/**
	 * ��Ӽ�����
	 * @param listener LifecycleListenerʵ��
	 */
	@Override
	public final void addLifecycleListener(LifecycleListener listener) {
		if(listener == null)
			return;
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * ��Ӽ���������
	 * @param listener LifecycleListenerʵ������
	 */
	public final void addLifecycleListener(LifecycleListener[] listeners) {
		
		if(listeners == null || listeners.length == 0)
			return;
		
		synchronized(listeners) {
			for(int i = 0; i < listeners.length; i++)
				this.listeners.add(listeners[i]);
		}
	}
	
	/**
	 * �Ƴ��¼�������
	 * @param listener ��Ҫ�Ƴ��ļ���������
	 */
	public final void removeLifecycleListener(LifecycleListener listener) {
		
		if(listener == null)
			return;
		
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * �������е��¼�����������
	 * @return ���������¼���������List����
	 */
	public final List<LifecycleListener> getLifecycleListeners() {
		return listeners;
	}
	
	/**
	 * �¼������󣬵��ø÷��������߼�����
	 * @param type ָ�����¼�����
	 * @param data �¼�����
	 */
	protected final void runLifecycleEvent(String type, Object data) {
		
		LifecycleEvent event = new LifecycleEvent(this, type, data);
		
		for(LifecycleListener listener : listeners) {
			listener.lifecycleEvent(event);
		}
	}
	
	/**
	 * �¼������󣬵��ø÷��������߼������¼�����Ϊ�ö����״̬
	 * @param data �¼�����
	 */
	protected final void runLifecycleEvent(Object data) {
		runLifecycleEvent(getLifecycleState().getLifecycleEvent(), data);
	}

}
