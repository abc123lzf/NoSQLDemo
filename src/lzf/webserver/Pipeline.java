package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:33:57
* @Description �ܵ��ӿ�
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
	public Valve getBase();
	
	/**
	 * ���û�������
	 * @param ��������ʵ��
	 */
	public void setBase(Valve valve);
	
	/**
	 * ��ܵ���ӷǻ�������
	 * @param valve �ǻ�������
	 */
	public void addValve(Valve valve);
}
