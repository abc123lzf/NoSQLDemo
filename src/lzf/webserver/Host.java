package lzf.webserver;

import java.io.File;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:38:31
* @Description 虚拟主机容器，父容器为Engine
*/
public interface Host extends Container {

	public File getWebappBaseFolder();
	
	public void setWebappBaseFolder(File folder);
}
