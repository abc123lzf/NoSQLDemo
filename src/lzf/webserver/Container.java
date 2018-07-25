package lzf.webserver;

import java.util.List;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��12�� ����4:56:16
 * @Description ���������ӿڣ����Ͳ���F��ʾ���������ͣ�S��ʾ���������� 
 */
public interface Container<F, S> extends Lifecycle {
	/**
	 * ����������¼�
	 */
	public static final String ADD_CHILD_EVENT = "add_child";
	/**
	 * �Ƴ��������¼�
	 */
	public static final String REMOVE_CHILD_EVENT = "remove_child";
	/**
	 * ��ǰ�����Ĺܵ���ӷ����¼�
	 */
	public static final String ADD_VALVE_EVENT = "add_valve";
	/**
	 * ��ǰ�����Ĺܵ��Ƴ������¼�
	 */
	public static final String REMOVE_VALVE_EVENT = "remove_valve";

	/**
	 * ���õ�ǰ����������
	 * @param name ������
	 */
	public void setName(String name);
	
	/**
	 * ���ص�ǰ����������
	 * @return �������ַ���
	 */
	public String getName();
	
	/**
	 * ��ȡ��ǰ�����Ĺܵ�
	 * @return Pipeline�ܵ�ʵ��(StandardPipeline)
	 */
	public Pipeline getPipeline();
	
	/**
	 * ���õ�ǰ�����������������ǰ��������������Jar�����ɸ���������������
	 * @param loader �������ʵ��������ΪWebappClassLoader
	 */
	public void setClassLoader(ClassLoader classLoader);
	
	/**
	 * ���ص�ǰ�������������
	 * @return �������ʵ��
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * ��ǰ���������������ע�⣺Wrapper�����������������
	 * @param container ��������
	 * @throws ��ӵĲ��ǵ�ǰ������������
	 */
	public void addChildContainer(S container) throws IllegalArgumentException;
	
	/**
	 * �Ƴ���������Wrapper������Ч
	 * @param container ��������
	 */
	public void removeChildContainer(S container);
	
	/**
	 * ��ȡ��������ע�⣺Engine������Ϊnull
	 */
	public F getParentContainer();
	
	/**
	 * �������������ƻ�ȡ������
	 * @param name ����������
	 */
	public S getChildContainer(String name);
	
	/**
	 * ��ȡ���е�������
	 * @return ����������������List����
	 */
	public List<S> getChildContainers();
	
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
	 * ��ȡ���е������¼�����������
	 * @return ���������������ļ���
	 */
	public List<ContainerListener> getContainerListeners();
}