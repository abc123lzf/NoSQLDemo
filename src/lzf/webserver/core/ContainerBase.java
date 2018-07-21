package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;
import lzf.webserver.Pipeline;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����11:23:32
* @Description ����������
*/
public abstract class ContainerBase extends LifecycleBase implements Container {
	
	//��������
	protected String name;
	//��������Engine����û�и�����
	protected Container parentContainer = null;
	//��ǰ�������������
	protected ClassLoader classLoader = null;
	//��ǰ���������Ĺܵ�
	protected Pipeline pipeline = new StandardPipeline(this);
	//��������Wrapperû��������
	protected List<Container> childContainers = new CopyOnWriteArrayList<>();
	//����������
	protected List<ContainerListener> containerListeners = new CopyOnWriteArrayList<>();
	
	/**
	 * ���õ�ǰ����������
	 * @param name ������
	 */
	@Override
	public final void setName(String name) {
		this.name = name;
	}
	
	/**
	 * ���ص�ǰ����������
	 * @return �������ַ���
	 */
	@Override
	public final String getName() {
		return this.name;
	}
	
	/**
	 * ���õ�ǰ�����������������ǰ��������������Jar�����ɸ���������������
	 * @param loader �������ʵ��������ΪWebappClassLoader
	 */
	@Override
	public final void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	/**
	 * ���ص�ǰ�������������
	 * @return �������ʵ��
	 */
	@Override
	public final ClassLoader getClassLoader() {
		return classLoader;
	}
	
	/**
	 * ��ȡ��������ע�⣺Engine������Ϊnull
	 */
	@Override
	public final Container getParentContainer() {
		return parentContainer;
	}
	
	/**
	 * ��ȡ��ǰ�����Ĺܵ�
	 * @return Pipeline�ܵ�ʵ��(StandardPipeline)
	 */
	@Override
	public final Pipeline getPipeline() {
		return pipeline;
	}
	
	/**
	 * ��������¼�������
	 * @param listener �����������࣬����lambda���ʽ
	 */
	@Override
	public final void addContainerListener(ContainerListener listener) {
		runContainerEvent(Container.ADD_CHILD_EVENT, null);
		containerListeners.add(listener);
	}
	
	
	/**
	 * ��ǰ���������������ע�⣺Wrapper�����������������
	 * @param container ��������
	 */
	@Override
	public void addChildContainer(Container container) throws IllegalArgumentException {
		addChildContainerCheck(container);
		childContainers.add(container);
	}
	
	/**
	 * ����Ƿ��ǺϷ���������
	 * @param container
	 * @throws IllegalArgumentException
	 */
	protected abstract void addChildContainerCheck(Container container) 
			throws IllegalArgumentException;
	
	/**
	 * �Ƴ���������Wrapper������Ч
	 * @param container ��������
	 */
	@Override
	public void removeChildContainer(Container container) {
		runContainerEvent(Container.REMOVE_CHILD_EVENT, null);
		childContainers.remove(container);
	}
	
	/**
	 * ��ȡ���е�������
	 * @return ����������������List����
	 */
	@Override
	public Container getChildContainer(String name) {
		for(Container c : childContainers) {
			if(c.getName().equals(name))
				return c;
		}
		return null;
	}
	
	@Override
	public List<Container> getChildContainers() {
		return childContainers;
	}
	
	/**
	 * ��ȡ���е������¼�����������
	 * @return ���������������ļ���
	 */
	@Override
	public final List<ContainerListener> getContainerListeners() {
		return containerListeners;
	}
	
	/**
	 * �Ƴ������¼�������
	 * @param listener ��Ҫ�Ƴ��������������� 
	 */
	@Override
	public final void removeContainerListener(ContainerListener listener) {
		containerListeners.remove(listener);
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
