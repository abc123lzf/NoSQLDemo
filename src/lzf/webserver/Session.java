package lzf.webserver;

import lzf.webserver.session.SessionManagerBase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����3:42:44
* @Description �Ự�ӿ�
*/
public interface Session {

	/**
	 * ��ȡSessionId
	 * @return SessionId
	 */
	public String getId();
	
	/**
	 * ��ȡ��Session����ʱ���
	 * @return ʱ���
	 */
	public long getCreationTime();
	
	/**
	 * ��ȡSession��������������ʱ��
	 * @return ʱ���
	 */
	public int getMaxInactiveInterval();
	
	/**
	 * ��ȡ��Session�������һ�η��ʵ�ʱ���
	 * @return ʱ���
	 */
	public long getLastAccessedTime();
	
	/**
	 * �������һ�η���ʱ�䣬ͬʱ�����isNew����
	 */
	public void updateLastAccessedTime();
	
	/**
	 * ����Session UUID
	 * @return �����ɵ�ID
	 */
	public String changeId();
	
	/**
	 * ��ȡ��Session����Ĺ�����
	 * @return Session���������
	 */
	public SessionManagerBase getSessionManager();
}
