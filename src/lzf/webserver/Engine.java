package lzf.webserver;
/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:36:41
* @Description ȫ�����������������ж��Hostʵ��
*/
public interface Engine extends Container {

	public void setService(Service service) throws LifecycleException;
	
	public Service getService();
}
