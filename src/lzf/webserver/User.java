package lzf.webserver;

import java.security.Principal;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月26日 下午9:33:11
* @Description 实现资源认证模块
*/
public interface User extends Principal {
	
	public String getFullName();
	
	public void setFullName();

	public String getUsername();
	
	public void setUsername(String name);
	
	public String getPassword();
	
	public void setPassword(String password);
	
	
}
