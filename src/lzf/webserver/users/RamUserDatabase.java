package lzf.webserver.users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import lzf.webserver.Group;
import lzf.webserver.Role;
import lzf.webserver.User;
import lzf.webserver.UserDatabase;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.startup.ServerConstant;
import lzf.webserver.util.StringManager;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月28日 下午9:30:34
 * @Description 内存用户数据库
 */
public class RamUserDatabase implements UserDatabase {

	private static final StringManager sm = StringManager.getManager(RamUserDatabase.class);

	private static final Log log = LogFactory.getLog(RamUserDatabase.class);

	static final String file_suffix = "_udb.xml";

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

	/**
     * @return 该用户数据库的ID，不同的用户数据库ID值应当不同
     */
	@Override
	public String getId() {
		return id;
	}

	/**
     * @return 该用户数据库包含的所有角色
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return 该用户数据库包含的所有用户
     */
	@Override
	public synchronized Iterator<User> getUsers() {
		return users.values().iterator();
	}

	/**
	 * @return 该用户数据库所有的用户组
	 */
	@Override
	public synchronized Iterator<Group> getGroups() {
		return groups.values().iterator();
	}

	/**
     * 关闭用户数据库
     * @throws Exception 当尝试关闭时发生异常
     */
	@Override
	public void close() throws Exception {
		save();

		synchronized (this) {
			users.clear();
			groups.clear();
		}
	}

	/**
     * 新建一个用户组
     * @param groupname 用户组名称
     * @param description 用户组描述
     * @return 新创建的用户组
     */
	@Override
	public Group createGroup(String groupname, String description) {

		if (groupname == null || groupname.length() == 0) {
			String msg = sm.getString("RamUserDatabase.createGroup.w0");
			log.warn(msg);
			throw new IllegalArgumentException(msg);
		}

		RamGroup group = new RamGroup(this, groupname, description);

		synchronized (groups) {
			groups.put(group.getName(), group);
		}

		return group;
	}

	/**
     * 新建一个角色对象
     * @param rolename 角色名称
     * @param description 角色描述
     * @return 新创建的角色
     */
	@Override
	public Role createRole(String rolename, String description) {

		if (rolename == null || rolename.length() == 0) {
			String msg = sm.getString("RamUserDatabase.createRole.w0");
			log.warn(msg);
			throw new IllegalArgumentException(msg);
		}

		RamRole role = new RamRole(this, rolename, description);

		synchronized (roles) {
			roles.put(role.getName(), role);
		}

		return role;
	}

	/**
     * 新建一个用户对象
     * @param username 用户名
     * @param password 密码
     * @param fullName 用户全名
     * @return 新创建的用户对象
     */
	@Override
	public User createUser(String username, String password, String fullName) {

		if (username == null || username.length() == 0) {
			String msg = sm.getString("RamUserDatabase.createUser.w0");
			log.warn(msg);
			throw new IllegalArgumentException(msg);
		}

		RamUser user = new RamUser(this, username, password, fullName);

		synchronized (users) {
			users.put(user.getUsername(), user);
		}

		return user;
	}

	/**
     * 根据用户组名查找组
     * @param groupname 用户组名称
     * @return 查找的用户组对象，如果没有找到则返回null
     */
	@Override
	public synchronized Group findGroup(String groupname) {
		return groups.get(groupname);
	}

	/**
     * 根据角色名称查找角色对象
     * @param rolename 角色名称
     * @return 查找到的角色对象，如果没有找到则返回null
     */
	@Override
	public synchronized Role findRole(String rolename) {
		return roles.get(rolename);
	}

	/**
     * 根据用户名查找用户对象
     * @param username 用户名
     * @return 查找到的用户对象。如果没有找到则返回null
     */
	@Override
	public synchronized User findUser(String username) {
		return users.get(username);
	}
	
	/**
     * 初始化数据库对象
     * @throws Exception 如果开启过程中抛出异常
     */
	@Override
	public void open() throws Exception {
		synchronized (groups) {
			synchronized (users) {
				groups.clear();
				users.clear();

				// TODO Read xml
			}
		}
	}
	
	/**
     * 移除用户组
     * @param group 用户组对象
     */
	@Override
	public void removeGroup(Group group) {

		synchronized (groups) {
			Iterator<User> users = getUsers();

			while (users.hasNext()) {
				User user = users.next();
				user.removeGroup(group);
			}

			groups.remove(group.getGroupname());
		}
	}

	/**
     * 移除角色对象
     * @param role 角色对象
     */
	@Override
	public void removeRole(Role role) {

		synchronized (roles) {
			Iterator<Group> groups = getGroups();

			while (groups.hasNext()) {
				Group group = groups.next();
				group.removeRole(role);
			}

			Iterator<User> users = getUsers();

			while (users.hasNext()) {
				User user = users.next();
				user.removeRole(role);
			}

			roles.remove(role.getRolename());
		}
	}

	/**
     * 移除用户
     * @param user 用户对象
     */
	@Override
	public synchronized void removeUser(User user) {
		users.remove(user.getUsername());
	}

	/**
     * 持久化该数据库
     * @throws Exception
     */
	@Override
	public void save() throws Exception {

		File file = new File(ServerConstant.getConstant().getSavedata(), getId() + file_suffix);

		if (!file.exists())
			file.createNewFile();
		
		PrintWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
			
			writer = new PrintWriter(osw);
	
			writer.println("<?xml version='1.0' encoding='utf-8'?>");
			writer.println("<server-users>");
			
			Iterator<?> values = null;
	        values = getRoles();
	        
	        while (values.hasNext()) {
	            writer.print("  ");
	            writer.println(values.next());
	        }
	        
	        values = getGroups();
	        
	        while (values.hasNext()) {
	            writer.print("  ");
	            writer.println(values.next());
	        }
	        
	        values = getUsers();
	        
	        while (values.hasNext()) {
	            writer.print("  ");
	            writer.println(((RamUser) values.next()));
	        }
	        
	        writer.println("<server-users>\"");

		} catch(IOException e) {
			log.warn(sm.getString("RamUserDatabase.save.w0", getId()), e);
		} finally {
			writer.close();
		}
	}
}
