package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午10:11:58
* @Description 容器类事件监听器
*/
@FunctionalInterface
public interface ContainerListener {

	public void containerEvent(ContainerEvent event);
}
