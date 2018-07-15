package lzf.webserver.session;

import lzf.webserver.Context;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����4:20:06
* @Description �Ự�����࣬ÿ��WebӦ�ö�Ӧһ���Ự������
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
