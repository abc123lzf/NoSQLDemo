package lzf.webserver;

import java.security.Principal;
import java.util.Iterator;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午7:55:09
* @Description 类说明
*/
public interface Group extends Principal {

	/**
	 * @return 该角色的描述
	 */
    public String getDescription();

    /**
     * @param description 该角色的描述
     */
    public void setDescription(String description);

    /**
     * @return 该用户组的名称
     */
    public String getGroupname();

    /**
     * @param groupname 该用户组的名称
     */
    public void setGroupname(String groupname);

    /**
     * @return 该用户组所有的角色
     */
    public Iterator<Role> getRoles();

    /**
     * @return 存储这个用户组所属的数据库
     */
    public UserDatabase getUserDatabase();

    /**
     * @return 这个用户组所有的用户迭代器
     */
    public Iterator<User> getUsers();

    /**
     * @param role 向该用户组添加角色
     */
    public void addRole(Role role);

    /**
     * 这个角色在该用户组存在吗
     * @param role Role角色对象
     * @return 这个角色在该用户组是否存在
     */
    public boolean isInRole(Role role);

    /**
     * 移除该用户组的角色
     * @param role 角色对象
     */
    public void removeRole(Role role);

    /**
     * 移除该用户组所有的角色
     */
    public void removeRoles();
}
