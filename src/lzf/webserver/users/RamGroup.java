package lzf.webserver.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lzf.webserver.Role;
import lzf.webserver.User;
import lzf.webserver.UserDatabase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午10:04:53
* @Description 内存用户组对象
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
     * @return 该用户组所有的角色
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return 存储这个用户组所属的数据库
     */
	@Override
	public UserDatabase getUserDatabase() {
		return database;
	}

	/**
     * @return 这个用户组所有的用户迭代器
     */
	@Override
	public synchronized Iterator<User> getUsers() {
		return users.values().iterator();
	}

	 /**
     * @param role 向该用户组添加角色
     */
	@Override
	public synchronized void addRole(Role role) {
		roles.put(role.getName(), role);
	}

	/**
     * 这个角色在该用户组存在吗
     * @param role Role角色对象
     * @return 这个角色在该用户组是否存在
     */
	@Override
	public boolean isInRole(Role role) {
		return roles.containsKey(role.getName());
	}

	/**
     * 移除该用户组的角色
     * @param role 角色对象
     */
	@Override
	public synchronized void removeRole(Role role) {
		roles.remove(role.getName());
	}
	
	/**
     * 移除该用户组所有的角色
     */
	@Override
	public synchronized void removeRoles() {
		roles.clear();
	}
}
