package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��9�� ����2:00:56
* @Description Lifecycle�����״̬
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
	
	//��������״̬����
	private final String lifecycleEvent;
	
	//�������ڲ���
	private final int step;
	
	//�ö����Ƿ����
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