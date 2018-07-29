package lzf.webserver.realm;

import java.security.Principal;

import lzf.webserver.Container;
import lzf.webserver.User;
import lzf.webserver.UserDatabase;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;
import lzf.webserver.users.RamUserDatabase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��29�� ����11:05:35
* @Description �û����ݿ���Դ��֤
*/
public class UserDatabaseRealm<T extends Container<?, ?>> extends RealmBase<T> {
	
	protected UserDatabase database = null;
	
	public UserDatabaseRealm(T container) {
		setContainer(container);
	}

	@Override
	public Principal authenticate(String username) {
		
		if(!getLifecycleState().isAvailable())
			return null;
		
		User user = database.findUser(username);
		
		if(user.getPassword() != null || !user.getPassword().equals("")) {
			return null;
		}
		
		return user;
	}

	@Override
	public Principal authenticate(String username, String password) {
		
		if(!getLifecycleState().isAvailable())
			return null;
		
		User user = database.findUser(username);
		
		if(user.getPassword().equals(password))
			return user;
		
		return null;
	}

	@Override
	public boolean hasResourcePermission(Request request, Response response) {
		
		return false;
	}

	@Override
	protected void initInternal() throws Exception {

	}

	@Override
	protected void startInternal() throws Exception {
		database = new RamUserDatabase();
	}

	@Override
	protected void stopInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destoryInternal() throws Exception {
		// TODO Auto-generated method stub

	}

}
