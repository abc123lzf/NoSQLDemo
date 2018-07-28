package lzf.webserver.users;

import lzf.webserver.UserDatabase;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月28日 下午9:53:59
 * @Description 内存Role对象
 */
public class RamRole extends RoleBase {

	protected final RamUserDatabase database;

	RamRole(RamUserDatabase database, String roleName, String description) {
		this.database = database;
		setRolename(roleName);
		setDescription(description);
	}

	/**
     * @return 存储该用户的数据库
     */
	@Override
	public UserDatabase getUserDatabase() {
		return this.database;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("<role rolename=\"");
		
		sb.append(roleName);
		sb.append("\"");
		
		if (description != null) {
			sb.append(" description=\"");
			sb.append(description);
			sb.append("\"");
		}
		
		sb.append("/>");
		return sb.toString();
	}
}
