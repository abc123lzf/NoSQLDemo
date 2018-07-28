package lzf.webserver;

import java.security.Principal;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��28�� ����7:55:23
* @Description ��ɫ���󣬺��û����ڶ�Զ��ϵ
* ������web.xml�ļ�������sercurity-constraint��������ﵽ��Դ���ʿ���
*/
public interface Role extends Principal {

	/**
	 * @return �ý�ɫ������
	 */
    public String getDescription();

    /**
     * @param description �ý�ɫ������
     */
    public void setDescription(String description);

    /**
     * @return �ý�ɫ������
     */
    public String getRolename();

    /**
     * @param rolename �ý�ɫ������
     */
    public void setRolename(String rolename);

    /**
     * @return �洢���û������ݿ�
     */
    public UserDatabase getUserDatabase();
}
