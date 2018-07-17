package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;


/**
* @author 李子帆
* @version 1.0
* @date 2018年7月17日 上午11:23:32
* @Description 容器抽象类
*/
public abstract class ContainerBase extends LifecycleBase implements Container {
	
	protected String name;
	
	protected Container parentContainer;
	
	protected ClassLoader classLoader;
	
	protected List<Container> childContainers = new CopyOnWriteArrayList<>();
	
	protected List<ContainerListener> containerListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public final void setName(String name) {
		this.name = name;
	}
	
	@Override
	public final String getName() {
		return this.name;
	}
	
	@Override
	public final void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	@Override
	public final ClassLoader getClassLoader() {
		return classLoader;
	}
	
	@Override
	public final List<ContainerListener> getContainerListeners() {
		return containerListeners;
	}
	
	@Override
	public final void addContainerListener(ContainerListener listener) {
		runContainerEvent(Container.ADD_CHILD_EVENT, null);
		containerListeners.add(listener);
	}
	
	@Override
	public void removeChildContainer(Container container) {
		runContainerEvent(Container.REMOVE_CHILD_EVENT, null);
		childContainers.remove(container);
	}
	
	@Override
	public Container getParentContainer() {
		return parentContainer;
	}
	
	@Override
	public Container getChildContainer(String name) {
		for(Container c : childContainers) {
			if(c.getName().equals(name))
				return c;
		}
		return null;
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
