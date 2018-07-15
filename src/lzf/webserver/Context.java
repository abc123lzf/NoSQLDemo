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

	public ServletContext getServletContext();
	
	public SessionManager getSessionManager();
}
