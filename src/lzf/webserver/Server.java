package lzf.webserver;

import java.io.File;
import java.util.List;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��13�� ����11:34:16
* @Description ������������
*/
public interface Server extends Lifecycle {

	//��ȡ���������û�Ŀ¼
	public File getMainPath();
	
	//���ü����رշ���������Ķ˿�
	public void setPort(int port);
	
	//��ȡ�ر�����Ķ˿�
	public int getPort();
	
	//���ùرշ�����ָ��
	public void setShutdownCommand(String cmd);
	
	//��ȡ�رշ�����ָ��
	public String getShutdownCommand();
	
	//����Service���
	public void setService(Service service);
	
	//����Service���ƻ�ȡService���
	public Service getService(String name);
	
	//��ȡService����б�
	public List<Service> getServices();
}
