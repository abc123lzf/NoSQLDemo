package lzf.webserver.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lzf.webserver.Container;
import lzf.webserver.ContainerEvent;
import lzf.webserver.ContainerListener;
import lzf.webserver.Pipeline;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��17�� ����11:23:32
* @Description ����������
*/
public abstract class ContainerBase<F, S> extends LifecycleBase implements Container<F, S> {
	
	//��������
	protected String name;
	
	//��������Engine����û�и�����
	protected F parentContainer = null;
	
	//��ǰ�������������
	protected ClassLoader classLoader = ContainerBase.class.getClassLoader();
	
	//��ǰ���������Ĺܵ�
	protected final Pipeline pipeline = new StandardPipeline(this);
	
	//��������Wrapperû��������
	protected final List<S> childContainers = new CopyOnWriteArrayList<>();
	
	//����������
	protected final List<ContainerListener> containerListeners = new CopyOnWriteArrayList<>();
	
	protected ContainerBase() {
		super();
	}
	
	protected ContainerBase(F parentConatiner) {
		this();
		this.parentContainer = parentConatiner;
	}
	
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
	public final F getParentContainer() {
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
		containerListeners.add(listener);
	}
	
	
	/**
	 * ��ǰ���������������ע�⣺Wrapper�����������������
	 * @param container ��������
	 */
	@Override
	public void addChildContainer(S container) throws IllegalArgumentException {
		childContainers.add(container);
		runContainerEvent(Container.ADD_CHILD_EVENT, container);
	}
	
	/**
	 * �Ƴ���������Wrapper������Ч
	 * @param container ��������
	 */
	@Override
	public void removeChildContainer(S container) {
		childContainers.remove(container);
		runContainerEvent(Container.REMOVE_CHILD_EVENT, container);
	}
	
	/**
	 * ��ȡ���е�������
	 * @return ����������������List����
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
