package lzf.webserver;

import java.io.File;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:38:31
* @Description ��������������������ΪEngine
*/
public interface Host extends Container {

	public File getWebappBaseFolder();
	
	public void setWebappBaseFolder(File folder);
}
