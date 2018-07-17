package lzf.webserver;

import java.util.List;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月9日 下午12:29:29
* @Description 生命周期接口，具有生命周期的服务器组件应该实现此接口
* @see lzf.webserver.LifecycleEvent
* @see lzf.webserver.LifecycleException
* @see lzf.webserver.LifecycleListener
* @see lzf.webserver.LifecycleState
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
	
	/**
	 * 添加生命周期事件监听器
	 * @param listener 监听器实现类
	 */
	public void addLifecycleListener(LifecycleListener listener);
	
	/**
	 * 获取所有的生命周期事件监听器
	 * @return 所有的事件监听器List集合
	 */
	public List<LifecycleListener> getLifecycleListeners();
	
	/**
	 * 初始化组件，会历经BEFORE_INIT_EVENT->AFTER_INIT_EVENT
	 * 必须在事件发生后调用LifecycleSupport的runLifecycleEvent方法
	 * @throws LifecycleException 当组件已经初始化后抛出异常 
	 */
	public void init() throws LifecycleException;
	
	/**
	 * 启动组件，历经BEFORE_START_EVENT->START_EVENT->AFTER_START_EVENT
	 * @throws LifecycleException
	 */
	public void start() throws LifecycleException;
	
	/**
	 * 停止组件，历经BEFORE_STOP_EVENT->STOP_EVENT->AFTER_STOP_EVENT
	 * @throws LifecycleException
	 */
	public void stop() throws LifecycleException;
	
	/**
	 * 销毁组件，仅在组件发生错误时调用
	 * 历经BEFORE_DESTORY_EVENT->AFTER_DESTORY_EVENT
	 * @throws LifecycleException
	 */
	public void destory() throws LifecycleException;
	
	/**
	 * 获取生命周期状态
	 * @return 状态枚举对象
	 */
	public LifecycleState getLifecycleState();
}