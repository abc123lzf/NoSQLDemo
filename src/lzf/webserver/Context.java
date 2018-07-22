package lzf.webserver;

import java.io.File;

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
	
	/**
	 * 获取URL解码的编码格式
	 * @return "UTF-8"
	 */
	public String getEncodedPath();
	
	/**
	 * @return 该web应用主目录
	 */
	public File getPath();
	
	/**
	 * @param path 设置该web应用的主目录
	 */
	public void setPath(File path);
	
	/**
	 * @return 该web应用支持运行时重加载吗
	 */
	public boolean getReloadable();
	
	/**
	 * @param reloadable 该web应用支持重加载吗
	 */
	public void setReloadable(boolean reloadable);
	
	/**
	 * @return Session过期时间毫秒数
	 */
	public int getSessionTimeout();
	
	/**
	 * @param timeout Session过期时间(单位：毫秒)
	 */
	public void setSessionTimeout(int timeout);
	
	/**
	 * @param webappVersion webapp版本号，由web.xml文件配置
	 */
	public void setWebappVersion(String webappVersion);
	
	/**
	 * @return 获取该webapp版本号，由web.xml文件配置
	 */
	public String getWebappVersion();
	
	/**
	 * @param encoding
	 */
	public void setRequestCharacterEncoding(String encoding);
	
	public String getRequestCharacterEncoding();
	
	public void setResponseCharacterEncoding(String encoding);
	
	public String getResponseCharacterEncoding();
}
