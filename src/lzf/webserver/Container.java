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

	/**
	 * 设置当前容器的名称
	 * @param name 容器名
	 */
	public void setName(String name);
	
	/**
	 * 返回当前容器的名称
	 */
	public String getName();
	
	/**
	 * 获取当前容器的管道
	 */
	public Pipeline getPipeline();
	
	/**
	 * 设置当前容器的类加载器，当前容器的子容器和Jar包都由该类加载器负责加载
	 * @param loader 类加载器实例，可以为WebappClassLoader
	 */
	public void setClassLoader(ClassLoader loader);
	
	/**
	 * 返回当前容器的类加载器
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * 向当前容器添加子容器，注意：Wrapper容器不可添加子容器
	 * @param container 子容器类
	 */
	public void addChildContainer(Container container);
	
	/**
	 * 移除子容器，Wrapper容器无效
	 * @param container 子容器类
	 */
	public void removeChildContainer(Container container);
	
	/**
	 * 获取父容器，注意：Engine父容器为null
	 */
	public Container getParentContainer();
	
	/**
	 * 根据子容器名称获取子容器
	 * @param name 子容器名称
	 */
	public Container getChildContainer(String name);
	
	/**
	 * 获取所有的子容器
	 */
	public List<Container> getChildContainers();
	
	/**
	 * 添加容器事件监听器
	 * @param listener 容器监听器类，可用lambda表达式
	 */
	public void addContainerListener(ContainerListener listener);
	
	/**
	 * 移除容器事件监听器
	 * @param listener 需要移除的容器监听器类 
	 */
	public void removeContainerListener(ContainerListener listener);
	
	/**
	 * 执行容器事件，该方法应遍历事件监听器列表并根据事件对象决定是否执行相应事件
	 * @param type 事件类型
	 * @param data 事件数据(参数，可为null)
	 */
	public void runContainerEvent(String type, Object data);
	
	/**
	 * 获取容器事件监听器列表
	 */
	public List<ContainerListener> getContainerListeners();
}