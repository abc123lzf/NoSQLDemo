package lzf.webserver;

import javax.servlet.ServletContext;

import lzf.webserver.session.HttpSessionManager;


/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:38:44
* @Description Web应用容器，对应一个Web应用
*/
public interface Context extends Container {

	/**
	 * 获取该Web应用对应的ServletContext对象
	 * @return ServletContext实例
	 */
	public ServletContext getServletContext();
	
	/**
	 * 获取该Web应用对应的Session管理器
	 * @return
	 */
	public HttpSessionManager getSessionManager();
	
	/**
	 * 获取默认的SessionID名称(例如JSESSIONID)
	 * @return 返回默认SessionID名
	 */
	public String getSessionIdName();
}
