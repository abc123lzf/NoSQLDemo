package lzf.webserver.session;

import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 下午4:20:06
* @Description 会话管理类，每个Web应用对应一个会话管理类
*/
public final class SessionManager {
	
	private final Context context;
	
	public SessionManager(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
}
