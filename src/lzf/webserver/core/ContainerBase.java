package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����11:23:32
* @Description ����������
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
	 * ִ�������¼����÷���Ӧ�����¼��������б������¼���������Ƿ�ִ����Ӧ�¼�
	 * @param type �¼�����
	 * @param data �¼�����(��������Ϊnull)
	 */
	protected void runContainerEvent(String type, Object data) {
		ContainerEvent event = new ContainerEvent(this, type, data);
		for(ContainerListener c : containerListeners) {
			c.containerEvent(event);
		}
	}
}
