package lzf.webserver;

import lzf.webserver.session.SessionManager;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����3:42:44
* @Description �Ự�ӿ�
*/
public interface Session {

	public String getId();
	
	public void setId(String id);
	
	public void setLiveTime(long time);
	
	public long getLiveTime();
	
	public SessionManager getSessionManager();
}
