package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月9日 下午2:00:56
* @Description Lifecycle对象的状态
*/
public enum LifecycleState {
	
	NEW(null, 0, false),
	
	INITIALIZING(Lifecycle.BEFORE_INIT_EVENT, 1, false),
	INITIALIZED(Lifecycle.AFTER_INIT_EVENT, 2, false),
	
	STARTING_PREP(Lifecycle.BEFORE_START_EVENT, 3, false),
	STARTING(Lifecycle.START_EVENT, 4, true),
	STARTED(Lifecycle.AFTER_START_EVENT, 5, true),
	
	STOPING_PREP(Lifecycle.BEFORE_STOP_EVENT, 6, true),
	STOPING(Lifecycle.STOP_EVENT, 7, false),
	STOPPED(Lifecycle.AFTER_STOP_EVENT, 8, false),
	
	DESTORYING(Lifecycle.BEFORE_DESTORY_EVENT, 9, false),
	DESTORYED(Lifecycle.AFTER_DESTORY_EVENT, 10, false);
	
	//生命周期状态名称
	private final String lifecycleEvent;
	
	//生命周期步骤
	private final int step;
	
	//该对象是否可用
	private final boolean available;
	
	private LifecycleState(String lifecycleEvent, int step, boolean avalible) {
		this.lifecycleEvent = lifecycleEvent;
		this.step = step;
		this.available = avalible;
	}
	
	public String getLifecycleEvent() {
		return lifecycleEvent;
	}
	
	public int getStep() {
		return step;
	}

	public boolean isAvailable() {
		return available;
	}
}