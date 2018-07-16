package lzf.webserver;

import javax.servlet.ServletContext;

import lzf.webserver.session.SessionManager;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:38:44
* @Description WebӦ����������Ӧһ��WebӦ��
*/
public interface Context extends Container {

	/**
	 * ��ȡ��WebӦ�ö�Ӧ��ServletContext����
	 * @return ServletContextʵ��
	 */
	public ServletContext getServletContext();
	
	/**
	 * ��ȡ��WebӦ�ö�Ӧ��Session������
	 * @return
	 */
	public SessionManager getSessionManager();
	
	/**
	 * ��ȡĬ�ϵ�SessionID����(����JSESSIONID)
	 * @return ����Ĭ��SessionID��
	 */
	public String getSessionIdName();
}
