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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��28�� ����9:30:34
 * @Description �ڴ��û����ݿ�
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
     * @return ���û����ݿ��ID����ͬ���û����ݿ�IDֵӦ����ͬ
     */
	@Override
	public String getId() {
		return id;
	}

	/**
     * @return ���û����ݿ���������н�ɫ
     */
	@Override
	public synchronized Iterator<Role> getRoles() {
		return roles.values().iterator();
	}

	/**
     * @return ���û����ݿ�����������û�
     */
	@Override
	public synchronized Iterator<User> getUsers() {
		return users.values().iterator();
	}

	/**
	 * @return ���û����ݿ����е��û���
	 */
	@Override
	public synchronized Iterator<Group> getGroups() {
		return groups.values().iterator();
	}

	/**
     * �ر��û����ݿ�
     * @throws Exception �����Թر�ʱ�����쳣
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
     * �½�һ���û���
     * @param groupname �û�������
     * @param description �û�������
     * @return �´������û���
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
     * �½�һ����ɫ����
     * @param rolename ��ɫ����
     * @param description ��ɫ����
     * @return �´����Ľ�ɫ
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
     * �½�һ���û�����
     * @param username �û���
     * @param password ����
     * @param fullName �û�ȫ��
     * @return �´������û�����
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
     * �����û�����������
     * @param groupname �û�������
     * @return ���ҵ��û���������û���ҵ��򷵻�null
     */
	@Override
	public synchronized Group findGroup(String groupname) {
		return groups.get(groupname);
	}

	/**
     * ���ݽ�ɫ���Ʋ��ҽ�ɫ����
     * @param rolename ��ɫ����
     * @return ���ҵ��Ľ�ɫ�������û���ҵ��򷵻�null
     */
	@Override
	public synchronized Role findRole(String rolename) {
		return roles.get(rolename);
	}

	/**
     * �����û��������û�����
     * @param username �û���
     * @return ���ҵ����û��������û���ҵ��򷵻�null
     */
	@Override
	public synchronized User findUser(String username) {
		return users.get(username);
	}
	
	/**
     * ��ʼ�����ݿ����
     * @throws Exception ��������������׳��쳣
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
     * �Ƴ��û���
     * @param group �û������
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
     * �Ƴ���ɫ����
     * @param role ��ɫ����
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
     * �Ƴ��û�
     * @param user �û�����
     */
	@Override
	public synchronized void removeUser(User user) {
		users.remove(user.getUsername());
	}

	/**
     * �־û������ݿ�
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
