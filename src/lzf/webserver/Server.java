package lzf.webserver;

import java.io.File;
import java.util.List;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月13日 上午11:34:16
* @Description 容器类最顶层组件
*/
public interface Server extends Lifecycle {

	/**
	 * 获取服务器主用户目录，这个用户目录会在新建Server实例时自动获取
	 * 即调用System.getProperties().get("user.dir")获取
	 * @return 主用户目录
	 */
	public File getMainPath();
	
	/**
	 * 设置监听关闭服务器命令的端口
	 * @param port 端口号，默认为9005
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	public void setPort(int port) throws LifecycleException;
	
	/**
	 * 获取关闭命令的端口
	 * @return 端口号
	 */
	public int getPort();
	
	/**
	 * 设置关闭服务器指令，如果没有设置则默认为SHUTDOWN
	 * @param cmd 关闭指令，当Socket监听到该字符串后会调用stop()方法
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	public void setShutdownCommand(String cmd) throws LifecycleException;
	
	/**
	 * 获取关闭服务器指令
	 * @return 关闭指令字符串
	 */
	public String getShutdownCommand();
	
	/**
	 * 设置Service组件
	 * @param service Service实例，一般为lzf.webserver.core.StandardServer
	 * @throws LifecycleException 当组件启动后调用该方法
	 */
	public void addService(Service service) throws LifecycleException;
	
	/**
	 * 根据Service名称获取Service组件
	 * @param name 名称
	 * @return 该Service实例，如果没有找到则返回null
	 */
	public Service getService(String name);
	
	/**
	 * 获取Service组件列表
	 * @return 所有的Service组件集合
	 */
	public List<Service> getServices();
}
