package lzf.webserver;

import java.util.Iterator;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����7:55:00
* @Description �洢�����û������ݿ�ӿ�
*/
public interface UserDatabase {

    /**
     * @return ���û����ݿ��ID����ͬ���û����ݿ�IDֵӦ����ͬ
     */
    public String getId();

    /**
     * @return ���û����ݿ���������н�ɫ
     */
    public Iterator<Role> getRoles();

    /**
     * @return ���û����ݿ�����������û�
     */
    public Iterator<User> getUsers();
    
    /**
	 * @return ���û����ݿ����е��û���
	 */
    public Iterator<Group> getGroups();

    /**
     * �ر��û����ݿ�
     * @throws Exception �����Թر�ʱ�����쳣
     */
    public void close() throws Exception;

    /**
     * �½�һ���û���
     * @param groupname �û�������
     * @param description �û�������
     * @return �´������û���
     */
    public Group createGroup(String groupname, String description);

    /**
     * �½�һ����ɫ����
     * @param rolename ��ɫ����
     * @param description ��ɫ����
     * @return �´����Ľ�ɫ
     */
    public Role createRole(String rolename, String description);

    /**
     * �½�һ���û�����
     * @param username �û���
     * @param password ����
     * @param fullName �û�ȫ��
     * @return �´������û�����
     */
    public User createUser(String username, String password, String fullName);

    /**
     * �����û�����������
     * @param groupname �û�������
     * @return ���ҵ��û���������û���ҵ��򷵻�null
     */
    public Group findGroup(String groupname);

    /**
     * ���ݽ�ɫ���Ʋ��ҽ�ɫ����
     * @param rolename ��ɫ����
     * @return ���ҵ��Ľ�ɫ�������û���ҵ��򷵻�null
     */
    public Role findRole(String rolename);

    /**
     * �����û��������û�����
     * @param username �û���
     * @return ���ҵ����û��������û���ҵ��򷵻�null
     */
    public User findUser(String username);

    /**
     * ��ʼ�����ݿ����
     * @throws Exception ��������������׳��쳣
     */
    public void open() throws Exception;

    /**
     * �Ƴ��û���
     * @param group �û������
     */
    public void removeGroup(Group group);

    /**
     * �Ƴ���ɫ����
     * @param role ��ɫ����
     */
    public void removeRole(Role role);

    /**
     * �Ƴ��û�
     * @param user �û�����
     */
    public void removeUser(User user);

    /**
     * �־û��û�����
     * @throws Exception
     */
    public void save() throws Exception;
	
}
