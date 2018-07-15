package lzf.webserver;

import lzf.webserver.session.SessionManager;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午3:42:44
* @Description 会话接口
*/
public interface Session {

	public String getId();
	
	public void setId(String id);
	
	public void setLiveTime(long time);
	
	public long getLiveTime();
	
	public SessionManager getSessionManager();
}
