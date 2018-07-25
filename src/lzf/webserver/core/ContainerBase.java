package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;
import lzf.webserver.Pipeline;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 上午11:23:32
* @Description 容器抽象类
*/
public abstract class ContainerBase<F, S> extends LifecycleBase implements Container<F, S> {
	
	//容器名称
	protected String name;
	
	//父容器，Engine容器没有父容器
	protected F parentContainer = null;
	
	//当前容器的类加载器
	protected ClassLoader classLoader = ContainerBase.class.getClassLoader();
	
	//当前容器所属的管道
	protected final Pipeline pipeline = new StandardPipeline(this);
	
	//子容器，Wrapper没有子容器
	protected final List<S> childContainers = new CopyOnWriteArrayList<>();
	
	//容器监听器
	protected final List<ContainerListener> containerListeners = new CopyOnWriteArrayList<>();
	
	protected ContainerBase() {
		super();
	}
	
	protected ContainerBase(F parentConatiner) {
		this();
		this.parentContainer = parentConatiner;
	}
	
	/**
	 * 设置当前容器的名称
	 * @param name 容器名
	 */
	@Override
	public final void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 返回当前容器的名称
	 * @return 容器名字符串
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * 设置当前容器的类加载器，当前容器的子容器和Jar包都由该类加载器负责加载
	 * @param loader 类加载器实例，可以为WebappClassLoader
	 */
	@Override
	public final void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	/**
	 * 返回当前容器的类加载器
	 * @return 类加载器实例
	 */
	@Override
	public final ClassLoader getClassLoader() {
		return classLoader;
	}
	
	/**
	 * 获取父容器，注意：Engine父容器为null
	 */
	@Override
	public final F getParentContainer() {
		return parentContainer;
	}
	
	/**
	 * 获取当前容器的管道
	 * @return Pipeline管道实例(StandardPipeline)
	 */
	@Override
	public final Pipeline getPipeline() {
		return pipeline;
	}
	
	/**
	 * 添加容器事件监听器
	 * @param listener 容器监听器类，可用lambda表达式
	 */
	@Override
	public final void addContainerListener(ContainerListener listener) {
		containerListeners.add(listener);
	}
	
	
	/**
	 * 向当前容器添加子容器，注意：Wrapper容器不可添加子容器
	 * @param container 子容器类
	 */
	@Override
	public void addChildContainer(S container) throws IllegalArgumentException {
		childContainers.add(container);
		runContainerEvent(Container.ADD_CHILD_EVENT, container);
	}
	
	/**
	 * 移除子容器，Wrapper容器无效
	 * @param container 子容器类
	 */
	@Override
	public void removeChildContainer(S container) {
		childContainers.remove(container);
		runContainerEvent(Container.REMOVE_CHILD_EVENT, container);
	}
	
	/**
	 * 获取所有的子容器
	 * @return 包含所有子容器的List集合
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public S getChildContainer(String name) {
		for(S c : childContainers) {
			if(((Container) c).getName().equals(name))
				return c;
		}
		return null;
	}
	
	@Override
	public List<S> getChildContainers() {
		return childContainers;
	}
	
	/**
	 * 获取所有的容器事件监听器集合
	 * @return 包含容器监听器的集合
	 */
	@Override
	public final List<ContainerListener> getContainerListeners() {
		return containerListeners;
	}
	
	/**
	 * 移除容器事件监听器
	 * @param listener 需要移除的容器监听器类 
	 */
	@Override
	public final void removeContainerListener(ContainerListener listener) {
		containerListeners.remove(listener);
	}
	
	/**
	 * 执行容器事件，该方法应遍历事件监听器列表并根据事件对象决定是否执行相应事件
	 * @param type 事件类型
	 * @param data 事件数据(参数，可为null)
	 */
	protected void runContainerEvent(String type, Object data) {
		ContainerEvent event = new ContainerEvent(this, type, data);
		for(ContainerListener c : containerListeners) {
			c.containerEvent(event);
		}
	}
}
