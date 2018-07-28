package lzf.webserver;

import java.security.Principal;
import java.util.Iterator;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��26�� ����9:33:11
* @Description ��Դ��֤ģ�飺�����û��ӿ�
* �����û�����һ��Role��һ��Role����һ��Group��һ��Group��Ӧһ��UserDatabase
*/
public interface User extends Principal {
	
	/**
	 * @return ���û�����ȫ��
	 */
    public String getFullName();

    /**
     * @param fullName �����û�ȫ��
     */
    public void setFullName(String fullName);

    /**
     * @return ���û���������ĵ�����
     */
    public Iterator<Group> getGroups();

    /**
     * @return �û�����
     */
    public String getPassword();

    /**
     * @param password ���û����������
     */
    public void setPassword(String password);

    /**
     * @return ���û�������Role������
     */
    public Iterator<Role> getRoles();
    
    /**
     * @return ���û��������û����ݿ�
     */
    public UserDatabase getUserDatabase();

    /**
     * @return �û���
     */
    public String getUsername();

    /**
     * @param username �����û���
     */
    public void setUsername(String username);

    /**
     * ������
     * @param group �����
     */
    public void addGroup(Group group);

    /**
     * ���ø��û��Ľ�ɫ
     * @param role Role����
     */
    public void addRole(Role role);

    /**
     * �жϸ��û��Ƿ��������
     * @param group �����
     * @return �û����������
     */
    public boolean isInGroup(Group group);

    /**
     * �û��������ɫ��
     * @param role Role����
     * @return �û����������ɫ��
     */
    public boolean isInRole(Role role);

    /**
     * @param group ���û���Ҫ�Ƴ�����
     */
    public void removeGroup(Group group);

    /**
     * @param �Ƴ����û����е���
     */
    public void removeGroups();

    /**
     * @param role �Ƴ�����û��Ľ�ɫ
     */
    public void removeRole(Role role);

    /**
     * �Ƴ�����û����еĽ�ɫ
     */
    public void removeRoles();	
}
