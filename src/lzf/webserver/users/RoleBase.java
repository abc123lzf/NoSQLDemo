package lzf.webserver.users;

import lzf.webserver.Role;
import lzf.webserver.UserDatabase;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����9:42:50
* @Description ��˵��
*/
public abstract class RoleBase implements Role {
	
	protected String roleName;
	
	protected String description;

	@Override
	public String getName() {
		return getRolename();
	}

	/**
	 * @return �ý�ɫ������
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
     * @param description �ý�ɫ������
     */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * @return �ý�ɫ������
     */
	@Override
	public String getRolename() {
		return roleName;
	}

	/**
     * @param rolename �ý�ɫ������
     */
	@Override
	public void setRolename(String rolename) {
		this.roleName = rolename;
	}

}
