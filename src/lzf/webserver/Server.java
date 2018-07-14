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

	//获取服务器主用户目录
	public File getMainPath();
	
	//设置监听关闭服务器命令的端口
	public void setPort(int port) throws LifecycleException;
	
	//获取关闭命令的端口
	public int getPort();
	
	//设置关闭服务器指令
	public void setShutdownCommand(String cmd) throws LifecycleException;
	
	//获取关闭服务器指令
	public String getShutdownCommand();
	
	//设置Service组件
	public void setService(Service service) throws LifecycleException;
	
	//根据Service名称获取Service组件
	public Service getService(String name);
	
	//获取Service组件列表
	public List<Service> getServices();
}
