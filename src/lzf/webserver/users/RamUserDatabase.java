package lzf.webserver.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import lzf.webserver.Group;
import lzf.webserver.Role;
import lzf.webserver.User;
import lzf.webserver.UserDatabase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午9:30:34
* @Description 内存用户数据库
*/
public class RamUserDatabase implements UserDatabase {
	
	protected final String id;
	
	protected final Map<String, Group> groups = new HashMap<>();
	
	protected final Map<String, Role> roles = new HashMap<>();
	
	protected final Map<String, User> users = new HashMap<>();
	
	public RamUserDatabase() {
		this.id = UUID.randomUUID().toString().replace("-", "");
	}
	
	public RamUserDatabase(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	@Override
	public synchronized Iterator<User> getUsers() {
		return users.values().iterator();
	}

	@Override
	public synchronized Iterator<Group> getGroups() {
		return groups.values().iterator();
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Group createGroup(String groupname, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role createRole(String rolename, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User createUser(String username, String password, String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group findGroup(String groupname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role findRole(String rolename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGroup(Group group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRole(Role role) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() throws Exception {
		// TODO Auto-generated method stub

	}

}
