package lzf.webserver;

import java.security.Principal;
import java.util.Iterator;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����7:55:09
* @Description ��˵��
*/
public interface Group extends Principal {

	/**
	 * @return �ý�ɫ������
	 */
    public String getDescription();

    /**
     * @param description �ý�ɫ������
     */
    public void setDescription(String description);

    /**
     * @return ���û��������
     */
    public String getGroupname();

    /**
     * @param groupname ���û��������
     */
    public void setGroupname(String groupname);

    /**
     * @return ���û������еĽ�ɫ
     */
    public Iterator<Role> getRoles();

    /**
     * @return �洢����û������������ݿ�
     */
    public UserDatabase getUserDatabase();

    /**
     * @return ����û������е��û�������
     */
    public Iterator<User> getUsers();

    /**
     * @param role ����û�����ӽ�ɫ
     */
    public void addRole(Role role);

    /**
     * �����ɫ�ڸ��û��������
     * @param role Role��ɫ����
     * @return �����ɫ�ڸ��û����Ƿ����
     */
    public boolean isInRole(Role role);

    /**
     * �Ƴ����û���Ľ�ɫ
     * @param role ��ɫ����
     */
    public void removeRole(Role role);

    /**
     * �Ƴ����û������еĽ�ɫ
     */
    public void removeRoles();
}
