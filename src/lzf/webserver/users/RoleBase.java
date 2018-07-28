package lzf.webserver.users;

import lzf.webserver.Role;
import lzf.webserver.UserDatabase;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月28日 下午9:42:50
* @Description 类说明
*/
public abstract class RoleBase implements Role {
	
	protected String roleName;
	
	protected String description;

	@Override
	public String getName() {
		return getRolename();
	}

	/**
	 * @return 该角色的描述
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
     * @param description 该角色的描述
     */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * @return 该角色的名称
     */
	@Override
	public String getRolename() {
		return roleName;
	}

	/**
     * @param rolename 该角色的名称
     */
	@Override
	public void setRolename(String rolename) {
		this.roleName = rolename;
	}

}
