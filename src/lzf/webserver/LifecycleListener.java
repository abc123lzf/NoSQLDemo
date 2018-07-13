package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月9日 下午1:20:43
* @Description 生命周期事件监听器接口
*/

@FunctionalInterface
public interface LifecycleListener {
	/**
	 * 在事件触发后由LifecycleSupport调用该方法
	 */
	public void lifecycleEvent(LifecycleEvent event);
	
}
