package lzf.webserver.users;

import lzf.webserver.User;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午9:19:36
* @Description 类说明
*/
public abstract class UserBase implements User {
	
	protected String fullName = null;
	
	protected String username = null;
	
	protected String password = null;

	@Override
	public String getFullName() {
		
		return fullName;
	}

	@Override
	public void setFullName(String fullName) {
		
		this.fullName = fullName;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public void setPassword(String password) {
		
		this.password = password;
	}

	@Override
	public String getUsername() {
		
		return username;
	}

	@Override
	public void setUsername(String username) {
		
		this.username = username;
	}

	@Override
	public String getName() {
		
		return getFullName();
	}

}
