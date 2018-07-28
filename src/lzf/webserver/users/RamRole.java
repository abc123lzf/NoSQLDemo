package lzf.webserver.users;

import lzf.webserver.UserDatabase;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��28�� ����9:53:59
 * @Description �ڴ�Role����
 */
public class RamRole extends RoleBase {

	protected final RamUserDatabase database;

	RamRole(RamUserDatabase database, String roleName, String description) {
		this.database = database;
		setRolename(roleName);
		setDescription(description);
	}

	/**
     * @return �洢���û������ݿ�
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
