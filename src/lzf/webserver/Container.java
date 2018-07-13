package lzf.webserver;

import java.util.List;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月12日 下午4:56:16
 * @Description 容器类主接口
 */
public interface Container extends Lifecycle {
	
	public static final String ADD_CHILD_EVENT = "add_child";
	public static final String REMOVE_CHILD_EVENT = "remove_child";
	public static final String ADD_VALVE_EVENT = "add_valve";
	public static final String REMOVE_VALVE_EVENT = "remove_valve";

	//设置容器的名称
	public void setName(String name);
	
	//获取容器的名称
	public String getName();
	
	//获取容器的管道
	public Pipeline getPipeline();
	
	//设置当前容器的类加载器
	public void setClassLoader(ClassLoader loader);
	
	//获取当前容器的类加载器
	public ClassLoader getClassLoader();
	
	//设置子容器，注意Wrapper容器不可添加子容器
	public void addChildContainer(Container container);
	
	//移除子容器
	public void removeChildContainer(Container container);
	
	//获取父容器
	public Container getParentContainer();
	
	//通过子容器的名称获取子容器
	public void getChildContainer(String name);
	
	//获取子容器列表
	public List<Container> getChildContainers();
	
	//添加容器事件监听器
	public void addContainerListener(ContainerListener listener);
	
	//移除容器事件监听器
	public void removeContainerListener(ContainerListener listener);
	
	//执行容器事件，该方法应遍历事件监听器列表并根据事件对象决定是否执行相应事件
	public void runContainerEvent(String type, Object data);
	
	//获取容器事件监听器的列表
	public List<ContainerListener> getContainerListeners();
}