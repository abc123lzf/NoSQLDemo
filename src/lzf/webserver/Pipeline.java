package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��12�� ����1:33:57
* @Description �ܵ��ӿ�
*/
public interface Pipeline {

	//���ص�һ������
	public Valve getFirst();
	
	//���ػ�������(�����һ������)
	public Valve getBase();
	
	//���û�������
	public void setBase(Valve valve);
	
	//��ܵ���ӷ���
	public void addValve(Valve valve);
}
