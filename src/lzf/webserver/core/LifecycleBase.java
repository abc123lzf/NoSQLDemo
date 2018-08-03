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
* @author 李子帆
* @version 1.0
* @date 2018年7月16日 下午7:18:34
* @Description 类说明
*/
public abstract class LifecycleBase implements Lifecycle {
	
	private static final StringManager sm = StringManager.getManager(LifecycleBase.class);

	private final Log log = LogFactory.getLog(LifecycleBase.class);
	
	private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();
	
	private volatile LifecycleState state = LifecycleState.NEW;

	/**
	 * 组件初始化
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
	 * 组件启动
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
	 * 组件停止
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
	 * 组件停止
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
	 * 初始化组件细节
	 * @throws LifecycleException 尝试在初始化后调用该方法
	 */
	protected abstract void initInternal() throws Exception;
	
	/**
	 * 启动组件细节
	 * @throws LifecycleException 尝试在未经初始化或者启动之后调用该方法
	 */
	protected abstract void startInternal() throws Exception;
	
	/**
	 * 停止组件细节
	 * @throws LifecycleException 尝试在未经初始化前或组件销毁后调用该方法
	 */
	protected abstract void stopInternal() throws Exception;
	
	/**
	 * 停止组件细节
	 * @throws LifecycleException 尝试在组件销毁后调用该方法
	 */
	protected abstract void destoryInternal() throws Exception;

	/**
	 * 获取生命周期状态
	 * @return LifecycleState状态对象
	 */
	@Override
	public final LifecycleState getLifecycleState() {
		return state;
	}
	
	protected final void setLifecycleState(LifecycleState state) {
		this.state = state;
	}
	
	/**
	 * 添加监听器
	 * @param listener LifecycleListener实例
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
	 * 添加监听器数组
	 * @param listener LifecycleListener实例数组
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
	 * 移除事件监听器
	 * @param listener 需要移除的监听器引用
	 */
	public final void removeLifecycleListener(LifecycleListener listener) {
		
		if(listener == null)
			return;
		
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * 返回所有的事件监听器引用
	 * @return 包含所有事件监听器的List集合
	 */
	public final List<LifecycleListener> getLifecycleListeners() {
		return listeners;
	}
	
	/**
	 * 事件发生后，调用该方法进行逻辑处理
	 * @param type 指定的事件类型
	 * @param data 事件数据
	 */
	protected final void runLifecycleEvent(String type, Object data) {
		
		LifecycleEvent event = new LifecycleEvent(this, type, data);
		
		for(LifecycleListener listener : listeners) {
			listener.lifecycleEvent(event);
		}
	}
	
	/**
	 * 事件发生后，调用该方法进行逻辑处理，事件类型为该对象的状态
	 * @param data 事件数据
	 */
	protected final void runLifecycleEvent(Object data) {
		runLifecycleEvent(getLifecycleState().getLifecycleEvent(), data);
	}

}
