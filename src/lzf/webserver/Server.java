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

	/**
	 * ��ȡ���������û�Ŀ¼������û�Ŀ¼�����½�Serverʵ��ʱ�Զ���ȡ
	 * ������System.getProperties().get("user.dir")��ȡ
	 * @return ���û�Ŀ¼
	 */
	public File getMainPath();
	
	/**
	 * ���ü����رշ���������Ķ˿�
	 * @param port �˿ںţ�Ĭ��Ϊ9005
	 * @throws LifecycleException �������������ø÷���
	 */
	public void setPort(int port) throws LifecycleException;
	
	/**
	 * ��ȡ�ر�����Ķ˿�
	 * @return �˿ں�
	 */
	public int getPort();
	
	/**
	 * ���ùرշ�����ָ����û��������Ĭ��ΪSHUTDOWN
	 * @param cmd �ر�ָ���Socket���������ַ���������stop()����
	 * @throws LifecycleException �������������ø÷���
	 */
	public void setShutdownCommand(String cmd) throws LifecycleException;
	
	/**
	 * ��ȡ�رշ�����ָ��
	 * @return �ر�ָ���ַ���
	 */
	public String getShutdownCommand();
	
	/**
	 * ����Service���
	 * @param service Serviceʵ����һ��Ϊlzf.webserver.core.StandardServer
	 * @throws LifecycleException �������������ø÷���
	 */
	public void addService(Service service) throws LifecycleException;
	
	/**
	 * ����Service���ƻ�ȡService���
	 * @param name ����
	 * @return ��Serviceʵ�������û���ҵ��򷵻�null
	 */
	public Service getService(String name);
	
	/**
	 * ��ȡService����б�
	 * @return ���е�Service�������
	 */
	public List<Service> getServices();
}
