package lzf.webserver;

import java.util.List;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月12日 下午4:56:16
 * @Description 容器类主接口，泛型参数F表示父容器类型，S表示子容器类型 
 */
public interface Container<F, S> extends Lifecycle {
	/**
	 * 添加子容器事件
	 */
	public static final String ADD_CHILD_EVENT = "add_child";
	/**
	 * 移除子容器事件
	 */
	public static final String REMOVE_CHILD_EVENT = "remove_child";
	/**
	 * 向当前容器的管道添加阀门事件
	 */
	public static final String ADD_VALVE_EVENT = "add_valve";
	/**
	 * 当前容器的管道移除阀门事件
	 */
	public static final String REMOVE_VALVE_EVENT = "remove_valve";

	/**
	 * 设置当前容器的名称
	 * @param name 容器名
	 */
	public void setName(String name);
	
	/**
	 * 返回当前容器的名称
	 * @return 容器名字符串
	 */
	public String getName();
	
	/**
	 * 获取当前容器的管道
	 * @return Pipeline管道实例(StandardPipeline)
	 */
	public Pipeline getPipeline();
	
	/**
	 * 设置当前容器的类加载器，当前容器的子容器和Jar包都由该类加载器负责加载
	 * @param loader 类加载器实例，可以为WebappClassLoader
	 */
	public void setClassLoader(ClassLoader classLoader);
	
	/**
	 * 返回当前容器的类加载器
	 * @return 类加载器实例
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * 向当前容器添加子容器，注意：Wrapper容器不可添加子容器
	 * @param container 子容器类
	 * @throws 添加的不是当前容器的子容器
	 */
	public void addChildContainer(S container) throws IllegalArgumentException;
	
	/**
	 * 移除子容器，Wrapper容器无效
	 * @param container 子容器类
	 */
	public void removeChildContainer(S container);
	
	/**
	 * 获取父容器，注意：Engine父容器为null
	 */
	public F getParentContainer();
	
	/**
	 * 根据子容器名称获取子容器
	 * @param name 子容器名称
	 */
	public S getChildContainer(String name);
	
	/**
	 * 获取所有的子容器
	 * @return 包含所有子容器的List集合
	 */
	public List<S> getChildContainers();
	
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
	 * 获取所有的容器事件监听器集合
	 * @return 包含容器监听器的集合
	 */
	public List<ContainerListener> getContainerListeners();
}