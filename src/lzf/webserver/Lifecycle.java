package lzf.webserver;

import java.util.List;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月9日 下午12:29:29
* @Description 生命周期接口，具有生命周期的服务器组件应该实现此接口
*/
public interface Lifecycle {
	
	public static final String BEFORE_INIT_EVENT = "before_init";
	public static final String AFTER_INIT_EVENT = "after_init";
	public static final String START_EVENT = "start";
	public static final String BEFORE_START_EVENT = "before_start";
	public static final String AFTER_START_EVENT = "after_start";
	public static final String STOP_EVENT = "stop";
	public static final String BEFORE_STOP_EVENT = "before_stop";
	public static final String AFTER_STOP_EVENT = "after_stop";
	public static final String BEFORE_DESTORY_EVENT = "before_destory";
	public static final String AFTER_DESTORY_EVENT = "after_destory";
	
	public void addLifecycleListener(LifecycleListener listener);
	public List<LifecycleListener> getLifecycleListeners();
	
	public void init() throws LifecycleException;
	public void start() throws LifecycleException;
	public void stop() throws LifecycleException;
	public void destory() throws LifecycleException;
	
	public LifecycleState getLifecycleState();
}