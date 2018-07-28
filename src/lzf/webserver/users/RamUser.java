package lzf.webserver.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lzf.webserver.Group;
import lzf.webserver.Role;
import lzf.webserver.UserDatabase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午9:18:04
* @Description 存储在内存中的用户对象
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
     * @return 该用户所属的组的迭代器
     */
	@Override
	public synchronized Iterator<Group> getGroups() {
		return groups.values().iterator();
	}

	/**
     * @return 该用户所属的Role迭代器
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return 该用户所属的用户数据库
     */
	@Override
	public UserDatabase getUserDatabase() {
		return database;
	}
	
	/**
     * 加入组
     * @param group 组对象
     */
	@Override
	public synchronized void addGroup(Group group) {
		groups.put(group.getName(), group);
	}

	/**
     * 设置该用户的角色
     * @param role Role对象
     */
	@Override
	public synchronized void addRole(Role role) {
		roles.put(role.getName(), role);
	}

	/**
     * 判断该用户是否在这个组
     * @param group 组对象
     * @return 用户在这个组吗
     */
	@Override
	public boolean isInGroup(Group group) {
		return groups.containsKey(group.getName());
	}

	/**
     * 用户是这个角色吗
     * @param role Role对象
     * @return 用户属于这个角色吗
     */
	@Override
	public boolean isInRole(Role role) {
		return roles.containsKey(role.getName());
	}

	/**
     * @param group 该用户需要移除的组
     */
	@Override
	public synchronized void removeGroup(Group group) {
		groups.remove(group.getName());
	}

	/**
     * @param 移除该用户所有的组
     */
	@Override
	public synchronized void removeGroups() {
		groups.clear();
	}

	/**
     * @param role 移除这个用户的角色
     */
	@Override
	public synchronized void removeRole(Role role) {
		roles.remove(role.getName());
	}

	/**
     * 移除这个用户所有的角色
     */
	@Override
	public synchronized void removeRoles() {
		roles.clear();
	}
}
