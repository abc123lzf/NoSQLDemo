package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Lifecycle;
import lzf.webserver.LifecycleEvent;
import lzf.webserver.LifecycleListener;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月11日 下午5:04:57
* @Description 生命周期支持类，用于于Lifecycle实现类成员，保存该类的事件监听器
*/
public final class LifecycleSupport {
	
	//所属Lifecycle对象
	private final Lifecycle lifecycle;
	
	//保存监听器的List
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
	 * 事件发生后，调用该方法进行逻辑处理
	 * @param type 事件类型
	 * @param data 事件数据
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
