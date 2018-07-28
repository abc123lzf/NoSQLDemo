package lzf.webserver;

import java.util.Iterator;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午7:55:00
* @Description 存储所有用户的数据库接口
*/
public interface UserDatabase {

    /**
     * @return 该用户数据库的ID，不同的用户数据库ID值应当不同
     */
    public String getId();

    /**
     * @return 该用户数据库包含的所有角色
     */
    public Iterator<Role> getRoles();

    /**
     * @return 该用户数据库包含的所有用户
     */
    public Iterator<User> getUsers();
    
    /**
	 * @return 该用户数据库所有的用户组
	 */
    public Iterator<Group> getGroups();

    /**
     * 关闭用户数据库
     * @throws Exception 当尝试关闭时发生异常
     */
    public void close() throws Exception;

    /**
     * 新建一个用户组
     * @param groupname 用户组名称
     * @param description 用户组描述
     * @return 新创建的用户组
     */
    public Group createGroup(String groupname, String description);

    /**
     * 新建一个角色对象
     * @param rolename 角色名称
     * @param description 角色描述
     * @return 新创建的角色
     */
    public Role createRole(String rolename, String description);

    /**
     * 新建一个用户对象
     * @param username 用户名
     * @param password 密码
     * @param fullName 用户全名
     * @return 新创建的用户对象
     */
    public User createUser(String username, String password, String fullName);

    /**
     * 根据用户组名查找组
     * @param groupname 用户组名称
     * @return 查找的用户组对象，如果没有找到则返回null
     */
    public Group findGroup(String groupname);

    /**
     * 根据角色名称查找角色对象
     * @param rolename 角色名称
     * @return 查找到的角色对象，如果没有找到则返回null
     */
    public Role findRole(String rolename);

    /**
     * 根据用户名查找用户对象
     * @param username 用户名
     * @return 查找到的用户对象。如果没有找到则返回null
     */
    public User findUser(String username);

    /**
     * 初始化数据库对象
     * @throws Exception 如果开启过程中抛出异常
     */
    public void open() throws Exception;

    /**
     * 移除用户组
     * @param group 用户组对象
     */
    public void removeGroup(Group group);

    /**
     * 移除角色对象
     * @param role 角色对象
     */
    public void removeRole(Role role);

    /**
     * 移除用户
     * @param user 用户对象
     */
    public void removeUser(User user);

    /**
     * 持久化用户对象
     * @throws Exception
     */
    public void save() throws Exception;
	
}
