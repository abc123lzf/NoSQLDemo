package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleEvent;
import lzf.webserver.LifecycleListener;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��11�� ����5:04:57
* @Description ��������֧���࣬������Lifecycleʵ�����Ա�����������¼�������
*/
public final class LifecycleSupport {
	
	//����Lifecycle����
	private final Lifecycle lifecycle;
	
	//�����������List
	private final List<LifecycleListener> listeners;
	
	public LifecycleSupport(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
		this.listeners = new CopyOnWriteArrayList<>();
	}
	
	public void addLifecycleListener(LifecycleListener listener) {
		if(listener == null)
			return;
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	public void addLifecycleListener(LifecycleListener[] listeners) {
		if(listeners == null || listeners.length == 0)
			return;
		synchronized(listeners) {
			for(int i = 0; i < listeners.length; i++)
				this.listeners.add(listeners[i]);
		}
	}
	
	public void removeLifecycleListener(LifecycleListener listener) {
		if(listener == null)
			return;
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	public List<LifecycleListener> getLifecycleListeners() {
		return listeners;
	}
	
	/**
	 * �¼������󣬵��ø÷��������߼�����
	 * @param type �¼�����
	 * @param data �¼�����
	 */
	public void runLifecycleEvent(String type, Object data) {
		LifecycleEvent event = new LifecycleEvent(this.lifecycle, type, data);
		for(LifecycleListener listener : listeners) {
			listener.lifecycleEvent(event);
		}
	}
	
	public void runLifecycleEvent(Object data) {
		runLifecycleEvent(lifecycle.getLifecycleState().getLifecycleEvent(), data);
	}
}
