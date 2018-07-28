package lzf.webserver.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lzf.webserver.Role;
import lzf.webserver.User;
import lzf.webserver.UserDatabase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����10:04:53
* @Description �ڴ��û������
*/
public final class RamGroup extends GroupBase {

	protected final RamUserDatabase database;
	
	RamGroup(RamUserDatabase database, String groupName, String description) {
		this.database = database;
		setGroupname(groupName);
		setDescription(description);
	}
	
	protected final Map<String, Role> roles = new HashMap<>();
	
	protected final Map<String, User> users = new HashMap<>();
	
	/**
     * @return ���û������еĽ�ɫ
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return �洢����û������������ݿ�
     */
	@Override
	public UserDatabase getUserDatabase() {
		return database;
	}

	/**
     * @return ����û������е��û�������
     */
	@Override
	public synchronized Iterator<User> getUsers() {
		return users.values().iterator();
	}

	 /**
     * @param role ����û�����ӽ�ɫ
     */
	@Override
	public synchronized void addRole(Role role) {
		roles.put(role.getName(), role);
	}

	/**
     * �����ɫ�ڸ��û��������
     * @param role Role��ɫ����
     * @return �����ɫ�ڸ��û����Ƿ����
     */
	@Override
	public boolean isInRole(Role role) {
		return roles.containsKey(role.getName());
	}

	/**
     * �Ƴ����û���Ľ�ɫ
     * @param role ��ɫ����
     */
	@Override
	public synchronized void removeRole(Role role) {
		roles.remove(role.getName());
	}
	
	/**
     * �Ƴ����û������еĽ�ɫ
     */
	@Override
	public synchronized void removeRoles() {
		roles.clear();
	}
}
