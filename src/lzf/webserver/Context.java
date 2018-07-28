package lzf.webserver;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;

import lzf.webserver.core.ApplicationFilterChain;
import lzf.webserver.core.ApplicationListenerContainer;
import lzf.webserver.core.WebappLoader;
import lzf.webserver.mapper.ContextMapper;
import lzf.webserver.session.HttpSessionManager;


/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:38:44
* @Description Web应用容器，对应一个Web应用
*/
public interface Context extends Container<Host, Wrapper> {
	
	//默认Session ID名称
	public static final String DEFAULT_SESSION_NAME = "JSESSIONID";

	/**
	 * 获取该Web应用对应的ServletContext对象
	 * @return ServletContext实例
	 */
	public ServletContext getServletContext();
	
	/**
	 * @return 该Web应用对应的Session管理器
	 */
	public HttpSessionManager getSessionManager();
	
	/**
	 * @return 该web应用的路由器
	 */
	public ContextMapper getMapper();
	
	/**
	 * 获取默认的SessionID名称(例如JSESSIONID)
	 * @return 返回默认SessionID名
	 */
	public String getSessionIdName();
	
	/**
	 * @param name 该web应用的默认SessionId名
	 */
	public void setSessionIdName(String name);
	
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
	 * 欢迎页面文件列表，由web.xml文件的welcome-file-list配置
	 * @return 所有的欢迎页面文件集合
	 */
	public List<String> getWelcomeFileList();
	
	/**
	 * 添加欢迎页面
	 * @param fileName 文件名称
	 */
	public void addWelcomeFile(String fileName);
	
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
	 * 根据SessionID生成该Context对应的Cookie
	 * @param sessionId SessionID
	 * @return 根据Context容器中的SessionCookieConfig包装的Cookie对象
	 */
	public Cookie createSessionCookie(String sessionId);
	
	/**
	 * @return 该Context容器的web应用载入器
	 */
	public WebappLoader getWebappLoader();
	
	/**
	 * @return 保存服务器发送给客户端保存SessionID的Cookie信息对象
	 */
	public SessionCookieConfig getSessionCookieConfig();
	
	/**
	 * @return 存储该WEB应用配置的监听器容器
	 */
	public ApplicationListenerContainer getListenerContainer();
	
	/**
	 * @return 该web应用对应FilterChain过滤器链
	 */
	public ApplicationFilterChain getFilterChain();
	
	/**
	 * @param encoding 该web应用请求体解码方式
	 */
	public void setRequestCharacterEncoding(String encoding);
	
	/**
	 * @return 该web应用请求体解码方式
	 */
	public String getRequestCharacterEncoding();
	
	/**
	 * @param encoding 该web应用响应体编码方式
	 */
	public void setResponseCharacterEncoding(String encoding);
	
	/**
	 * @return 该web应用响应体编码方式
	 */
	public String getResponseCharacterEncoding();
}
