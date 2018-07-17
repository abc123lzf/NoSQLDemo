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
	 */
	public void setBasic(Valve valve);
	
	/**
	 * ��ܵ���ӷǻ������ţ�����ڷ�����ǰ��
	 * @param valve �ǻ�������
	 */
	public void addValve(Valve valve);
	
	/**
	 * ��ȡ�ܵ������з��ŵ�����
	 * @return List����
	 */
	public Valve[] getValves();
	
	/**
	 * �Ƴ�ָ���ķ��ţ���û���ҵ������κε���
	 * @param valve ָ���ķ��Ŷ���
	 */
	public void removeValve(Valve valve);
	
	/**
	 * ���ظùܵ�����������
	 * @return ����ʵ��������ΪEngine��Host��Context��Wrapper
	 */
	public Container getContainer();
	
	/**
	 * ���øùܵ�����������
	 * @param container ����ʵ��������ΪEngine��Host��Context��Wrapper
	 */
	public void setContainer(Container container);
}