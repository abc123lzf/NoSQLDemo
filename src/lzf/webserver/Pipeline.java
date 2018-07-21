package lzf.webserver;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:33:57
* @Description �ܵ��ӿ�
* �ܵ�Ӧ������Container�����У���Container����������еķ��ţ����л�������Ӧλ�����
* �ǻ������Ű����˳�����ε���(�������ӵķ��ſ�ʼ)���������ŵ�����ɺ��ٵ�����һ�������ĵ�һ������
*/
public interface Pipeline {

	/**
	 * ���ص�һ������
	 * @return ��һ������
	 */
	public Valve getFirst();
	
	/**
	 * ���ػ�������(�����һ������)
	 * @return ��������
	 */
	public Valve getBasic();
	
	/**
	 * ���û�������(�����һ������)
	 * @param ��������ʵ��
	 * @throws LifecycleException ������Ѿ���������ô˷���
	 */
	public void setBasic(Valve valve) throws LifecycleException;
	
	/**
	 * ��ܵ���ӷǻ������ţ�����ڷ�����ǰ��
	 * @param valve �ǻ�������
	 */
	public void addValve(Valve valve) throws LifecycleException;
	
	/**
	 * ��ȡ�ܵ������з��ŵ�����
	 * @return List����
	 * @throws LifecycleException ������Ѿ���������ô˷���
	 */
	public Valve[] getValves();
	
	/**
	 * �Ƴ�ָ���ķ��ţ���û���ҵ������κε���
	 * @param valve ָ���ķ��Ŷ���
	 * @throws LifecycleException ������Ѿ���������ô˷���
	 */
	public void removeValve(Valve valve) throws LifecycleException;
	
	/**
	 * ���ظùܵ�����������
	 * @return ����ʵ��������ΪEngine��Host��Context��Wrapper
	 */
	public Container getContainer();
	
	/**
	 * ���øùܵ�����������
	 * @param container ����ʵ��������ΪEngine��Host��Context��Wrapper
	 * @throws LifecycleException ������Ѿ���������ô˷���
	 */
	public void setContainer(Container container) throws LifecycleException;
}