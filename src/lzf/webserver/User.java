package lzf.webserver;

import java.security.Principal;
import java.util.Iterator;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月26日 下午9:33:11
* @Description 资源认证模块：单个用户接口
* 单个用户属于一个Role，一个Role属于一个Group，一个Group对应一个UserDatabase
*/
public interface User extends Principal {
	
	/**
	 * @return 该用户对象全名
	 */
    public String getFullName();

    /**
     * @param fullName 设置用户全名
     */
    public void setFullName(String fullName);

    /**
     * @return 该用户所属的组的迭代器
     */
    public Iterator<Group> getGroups();

    /**
     * @return 用户密码
     */
    public String getPassword();

    /**
     * @param password 该用户对象的密码
     */
    public void setPassword(String password);

    /**
     * @return 该用户所属的Role迭代器
     */
    public Iterator<Role> getRoles();
    
    /**
     * @return 该用户所属的用户数据库
     */
    public UserDatabase getUserDatabase();

    /**
     * @return 用户名
     */
    public String getUsername();

    /**
     * @param username 设置用户名
     */
    public void setUsername(String username);

    /**
     * 加入组
     * @param group 组对象
     */
    public void addGroup(Group group);

    /**
     * 设置该用户的角色
     * @param role Role对象
     */
    public void addRole(Role role);

    /**
     * 判断该用户是否在这个组
     * @param group 组对象
     * @return 用户在这个组吗
     */
    public boolean isInGroup(Group group);

    /**
     * 用户是这个角色吗
     * @param role Role对象
     * @return 用户属于这个角色吗
     */
    public boolean isInRole(Role role);

    /**
     * @param group 该用户需要移除的组
     */
    public void removeGroup(Group group);

    /**
     * @param 移除该用户所有的组
     */
    public void removeGroups();

    /**
     * @param role 移除这个用户的角色
     */
    public void removeRole(Role role);

    /**
     * 移除这个用户所有的角色
     */
    public void removeRoles();	
}
