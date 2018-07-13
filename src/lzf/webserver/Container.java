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

	//��������������
	public void setName(String name);
	
	//��ȡ����������
	public String getName();
	
	//��ȡ�����Ĺܵ�
	public Pipeline getPipeline();
	
	//���õ�ǰ�������������
	public void setClassLoader(ClassLoader loader);
	
	//��ȡ��ǰ�������������
	public ClassLoader getClassLoader();
	
	//������������ע��Wrapper�����������������
	public void addChildContainer(Container container);
	
	//�Ƴ�������
	public void removeChildContainer(Container container);
	
	//��ȡ������
	public Container getParentContainer();
	
	//ͨ�������������ƻ�ȡ������
	public void getChildContainer(String name);
	
	//��ȡ�������б�
	public List<Container> getChildContainers();
	
	//��������¼�������
	public void addContainerListener(ContainerListener listener);
	
	//�Ƴ������¼�������
	public void removeContainerListener(ContainerListener listener);
	
	//ִ�������¼����÷���Ӧ�����¼��������б������¼���������Ƿ�ִ����Ӧ�¼�
	public void runContainerEvent(String type, Object data);
	
	//��ȡ�����¼����������б�
	public List<ContainerListener> getContainerListeners();
}