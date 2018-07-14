package lzf.webserver;

import java.util.List;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��12�� ����4:56:16
 * @Description ���������ӿ�
 */
public interface Container extends Lifecycle {
	
	public static final String ADD_CHILD_EVENT = "add_child";
	public static final String REMOVE_CHILD_EVENT = "remove_child";
	public static final String ADD_VALVE_EVENT = "add_valve";
	public static final String REMOVE_VALVE_EVENT = "remove_valve";

	/**
	 * ���õ�ǰ����������
	 * @param name ������
	 */
	public void setName(String name);
	
	/**
	 * ���ص�ǰ����������
	 */
	public String getName();
	
	/**
	 * ��ȡ��ǰ�����Ĺܵ�
	 */
	public Pipeline getPipeline();
	
	/**
	 * ���õ�ǰ�����������������ǰ��������������Jar�����ɸ���������������
	 * @param loader �������ʵ��������ΪWebappClassLoader
	 */
	public void setClassLoader(ClassLoader loader);
	
	/**
	 * ���ص�ǰ�������������
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * ��ǰ���������������ע�⣺Wrapper�����������������
	 * @param container ��������
	 */
	public void addChildContainer(Container container);
	
	/**
	 * �Ƴ���������Wrapper������Ч
	 * @param container ��������
	 */
	public void removeChildContainer(Container container);
	
	/**
	 * ��ȡ��������ע�⣺Engine������Ϊnull
	 */
	public Container getParentContainer();
	
	/**
	 * �������������ƻ�ȡ������
	 * @param name ����������
	 */
	public Container getChildContainer(String name);
	
	/**
	 * ��ȡ���е�������
	 */
	public List<Container> getChildContainers();
	
	/**
	 * ��������¼�������
	 * @param listener �����������࣬����lambda���ʽ
	 */
	public void addContainerListener(ContainerListener listener);
	
	/**
	 * �Ƴ������¼�������
	 * @param listener ��Ҫ�Ƴ��������������� 
	 */
	public void removeContainerListener(ContainerListener listener);
	
	/**
	 * ִ�������¼����÷���Ӧ�����¼��������б������¼���������Ƿ�ִ����Ӧ�¼�
	 * @param type �¼�����
	 * @param data �¼�����(��������Ϊnull)
	 */
	public void runContainerEvent(String type, Object data);
	
	/**
	 * ��ȡ�����¼��������б�
	 */
	public List<ContainerListener> getContainerListeners();
}