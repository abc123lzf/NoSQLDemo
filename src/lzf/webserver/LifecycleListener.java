package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��9�� ����1:20:43
* @Description ���������¼��������ӿ�
*/

@FunctionalInterface
public interface LifecycleListener {
	/**
	 * ���¼���������LifecycleSupport���ø÷���
	 */
	public void lifecycleEvent(LifecycleEvent event);
	
}
