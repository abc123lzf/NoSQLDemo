package lzf.webserver;

import javax.servlet.Servlet;
import javax.servlet.ServletException;


/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:39:00
* @Description 最底层容器，保存单个JSP、Servlet实例
*/
public interface Wrapper extends Container {

	/**
	 * 返回该Servlet可用的时间戳，如果Request请求到达的时间小于它，那么该Servlet不可用
	 * 返回404错误
	 * @return 时间戳
	 */
	public long getAvailable();
	
	/**
	 * 设置该Servlet可用时间戳，只有GMT时间超过该时间，该Servlet才可被访问
	 * @param available 可用时间戳
	 */
	public void setAvailable(long available);
	
	/**
	 * 标记容器是否在启动的时候就加载这个servlet
	 * 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet，并且值越小加载优先级越高，
	 * 当是一个负数时或者没有指定时，则指示容器在该servlet被选择时才加载，在web.xml中的load-on-startup设置
	 * @return load-on-startup参数值
	 */
	public int getLoadOnStartup();
	
	/**
	 * 标记容器是否在启动的时候就加载这个servlet
	 * 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet，并且值越小加载优先级越高，
	 * 当是一个负数时或者没有指定时，则指示容器在该servlet被选择时才加载，在web.xml中的load-on-startup设置
	 * @param value web.xml中的load-on-startup参数值
	 */
	public void setLoadOnStartup(int value);
	
	/**
	 * 返回这个Servlet类名
	 * @return 类名字符串
	 */
	public String getServletClass();
	
	/**
	 * 设置Servlet类名，由web.xml文件的servlet-class制定
	 * @param servletClass Servlet类名
	 */
	public void setServletClass(String servletClass);
	
	/**
	 * 这个Servlet目前可用吗
	 * @return true表示不可用
	 */
	public boolean isUnavailable();
	
	/**
	 * 添加Servlet初始化参数，此参数应由web.xml文件init-parameter指定
	 * @param name 参数名
	 * @param value 参数值
	 */
	public void addInitParameter(String name, String value);
	
	/**
	 * 根据参数名获取参数值，此参数应由web.xml文件init-parameter指定
	 * @param name
	 * @return 参数值
	 */
	public String getInitParameter(String name);
	
	/**
	 * @return 包含所有参数名的字符串数组
	 */
	public String[] getInitParameters();
	
	/**
	 * 根据参数名移除参数值
	 * @param name 参数名
	 */
	public void removeInitParameter(String name);
	
	/**
	 * 复制一个Servlet实例
	 * @return 新复制的Servlet
	 * @throws ServletException
	 */
	public Servlet allocate() throws ServletException;
	
	/*
	 * 初始化一个Servlet实例
	 */
	public void load() throws ServletException;
	
	/**
	 * 卸载当前Wrapper中所有的Servlet
	 * @throws ServletException
	 */
	public void unload() throws ServletException;
	
	/**
	 * 添加Servlet对象
	 * @param servlet
	 */
	public void addServlet(Servlet servlet);
	
	/**
	 * @return 当前Servlet实例
	 */
	public Servlet getServlet();
	
	/**
	 * @return 这个Servlet是单线程模式吗
	 */
	public boolean isSingleThreadModel();
	
}
