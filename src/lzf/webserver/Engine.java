package lzf.webserver;
/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:36:41
* @Description 全局引擎容器，可用有多个Host实例
*/
public interface Engine extends Container {

	public void setService(Service service) throws LifecycleException;
	
	public Service getService();
}
