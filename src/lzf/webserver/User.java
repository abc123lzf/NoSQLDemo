package lzf.webserver;

import java.security.Principal;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��26�� ����9:33:11
* @Description ʵ����Դ��֤ģ��
*/
public interface User extends Principal {
	
	public String getFullName();
	
	public void setFullName();

	public String getUsername();
	
	public void setUsername(String name);
	
	public String getPassword();
	
	public void setPassword(String password);
	
	
}
