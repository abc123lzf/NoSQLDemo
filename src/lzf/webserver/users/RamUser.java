package lzf.webserver.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lzf.webserver.Group;
import lzf.webserver.Role;
import lzf.webserver.UserDatabase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����9:18:04
* @Description �洢���ڴ��е��û�����
*/
public class RamUser extends UserBase {
	
	protected final RamUserDatabase database;
	
	RamUser(RamUserDatabase database, String username, String password, String fullName) {
		super();
		this.database = database;
		setPassword(password);
		setFullName(fullName);
	}

	protected final Map<String, Group> groups = new HashMap<>();
	
	protected final Map<String, Role> roles = new HashMap<>();
	
	/**
     * @return ���û���������ĵ�����
     */
	@Override
	public synchronized Iterator<Group> getGroups() {
		return groups.values().iterator();
	}

	/**
     * @return ���û�������Role������
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return ���û��������û����ݿ�
     */
	@Override
	public UserDatabase getUserDatabase() {
		return database;
	}
	
	/**
     * ������
     * @param group �����
     */
	@Override
	public synchronized void addGroup(Group group) {
		groups.put(group.getName(), group);
	}

	/**
     * ���ø��û��Ľ�ɫ
     * @param role Role����
     */
	@Override
	public synchronized void addRole(Role role) {
		roles.put(role.getName(), role);
	}

	/**
     * �жϸ��û��Ƿ��������
     * @param group �����
     * @return �û����������
     */
	@Override
	public boolean isInGroup(Group group) {
		return groups.containsKey(group.getName());
	}

	/**
     * �û��������ɫ��
     * @param role Role����
     * @return �û����������ɫ��
     */
	@Override
	public boolean isInRole(Role role) {
		return roles.containsKey(role.getName());
	}

	/**
     * @param group ���û���Ҫ�Ƴ�����
     */
	@Override
	public synchronized void removeGroup(Group group) {
		groups.remove(group.getName());
	}

	/**
     * @param �Ƴ����û����е���
     */
	@Override
	public synchronized void removeGroups() {
		groups.clear();
	}

	/**
     * @param role �Ƴ�����û��Ľ�ɫ
     */
	@Override
	public synchronized void removeRole(Role role) {
		roles.remove(role.getName());
	}

	/**
     * �Ƴ�����û����еĽ�ɫ
     */
	@Override
	public synchronized void removeRoles() {
		roles.clear();
	}
}
