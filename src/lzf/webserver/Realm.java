package lzf.webserver;

import java.security.Principal;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��29�� ����10:25:02
* @Description ��Դ��֤ģ�飬Engine��Host��ContextӦ��ӵ�����ģ��
* ���Ͳ���TΪ�������������
*/
public interface Realm<T extends Container<?, ?>> {

	/**
	 * @return ����Դ��֤ģ������������
	 */
	public T getContainer();
	
	/**
	 * @param container ����Դ��֤ģ������������
	 */
	public void setContainer(T container);
	
	/**
	 * ͨ���û������������֤
	 * @param username �û���
	 * @return Principal�������û��ͨ�������֤�򷵻�null
	 */
	public Principal authenticate(String username);
	
	/**
	 * ͨ���û��������������֤
	 * @param username �û���
	 * @param password ����
	 * @return Principal�������û��ͨ�������֤�򷵻�null
	 */
	public Principal authenticate(String username, String password);
	
	/**
	 * �ж��Ƿ���Է��������Դ
	 * @param request �����������
	 * @param response ������Ӧ����
	 * @param context ����ӳ���Context����
	 * @return ����������Է��������Դ��
	 */
	public boolean hasResourcePermission(Request request, Response response);
	
	/**
	 * @return ����Դ��֤ģ�������
	 */
	public boolean isAvailable();
}
