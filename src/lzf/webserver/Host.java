package lzf.webserver;

import java.io.File;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:38:31
* @Description ��������������������ΪEngine
*/
public interface Host extends Container {

	/**
	 * @return ��������Ӧ��webӦ����Ŀ¼
	 */
	public File getWebappBaseFolder();
	
	/**
	 * @param folder ��������Ӧ��webӦ��Ŀ¼
	 */
	public void setWebappBaseFolder(File folder);
}
