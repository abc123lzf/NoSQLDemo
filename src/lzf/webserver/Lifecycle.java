package lzf.webserver;

import java.util.List;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��9�� ����12:29:29
* @Description �������ڽӿڣ������������ڵķ��������Ӧ��ʵ�ִ˽ӿ�
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
	 * ������������¼�������
	 * @param listener ������ʵ����
	 */
	public void addLifecycleListener(LifecycleListener listener);
	
	/**
	 * ��ȡ���е����������¼�������
	 * @return ���е��¼�������List����
	 */
	public List<LifecycleListener> getLifecycleListeners();
	
	/**
	 * ��ʼ�������������BEFORE_INIT_EVENT->AFTER_INIT_EVENT
	 * �������¼����������LifecycleSupport��runLifecycleEvent����
	 * @throws LifecycleException ������Ѿ���ʼ�����׳��쳣 
	 */
	public void init() throws LifecycleException;
	
	/**
	 * �������������BEFORE_START_EVENT->START_EVENT->AFTER_START_EVENT
	 * @throws LifecycleException
	 */
	public void start() throws LifecycleException;
	
	/**
	 * ֹͣ���������BEFORE_STOP_EVENT->STOP_EVENT->AFTER_STOP_EVENT
	 * @throws LifecycleException
	 */
	public void stop() throws LifecycleException;
	
	/**
	 * ������������������������ʱ����
	 * ����BEFORE_DESTORY_EVENT->AFTER_DESTORY_EVENT
	 * @throws LifecycleException
	 */
	public void destory() throws LifecycleException;
	
	/**
	 * ��ȡ��������״̬
	 * @return ״̬ö�ٶ���
	 */
	public LifecycleState getLifecycleState();
}