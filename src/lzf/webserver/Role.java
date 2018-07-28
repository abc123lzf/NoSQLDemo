package lzf.webserver;

import java.security.Principal;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午7:55:23
* @Description 角色对象，和用户属于多对多关系
* 可以在web.xml文件中配置sercurity-constraint配置项，来达到资源访问控制
*/
public interface Role extends Principal {

	/**
	 * @return 该角色的描述
	 */
    public String getDescription();

    /**
     * @param description 该角色的描述
     */
    public void setDescription(String description);

    /**
     * @return 该角色的名称
     */
    public String getRolename();

    /**
     * @param rolename 该角色的名称
     */
    public void setRolename(String rolename);

    /**
     * @return 存储该用户的数据库
     */
    public UserDatabase getUserDatabase();
}
