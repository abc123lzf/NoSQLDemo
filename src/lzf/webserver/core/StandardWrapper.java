package lzf.webserver.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午5:39:04
* @Description 最小的容器，用于保存单个Servlet
*/
public class StandardWrapper extends ContainerBase implements Wrapper {
	
	private final Context context;
	
	private long availableTime = 0L;
	
	private int loadOnStartup = 0;
	
	private String servletClass;
	
	private Servlet servlet;
	
	private final Map<String, String> parameterMap = new LinkedHashMap<>();
	
	public StandardWrapper(Context context) {
		this.context = context;
	}

	/**
	 * 返回该Servlet可用的时间戳，如果Request请求到达的时间小于它，那么该Servlet不可用
	 * 返回404错误
	 * @return 时间戳
	 */
	@Override
	public long getAvailable() {
		return availableTime;
	}

	/**
	 * 设置该Servlet可用时间戳，只有GMT时间超过该时间，该Servlet才可被访问
	 * @param available 可用时间戳
	 */
	@Override
	public void setAvailable(long available) {
		this.availableTime = available;
	}

	/**
	 * 标记容器是否在启动的时候就加载这个servlet
	 * 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet，并且值越小加载优先级越高，
	 * 当是一个负数时或者没有指定时，则指示容器在该servlet被选择时才加载，在web.xml中的load-on-startup设置
	 * @return load-on-startup参数值
	 */
	@Override
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	/**
	 * 标记容器是否在启动的时候就加载这个servlet
	 * 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet，并且值越小加载优先级越高，
	 * 当是一个负数时或者没有指定时，则指示容器在该servlet被选择时才加载，在web.xml中的load-on-startup设置
	 * @param value web.xml中的load-on-startup参数值
	 */
	@Override
	public void setLoadOnStartup(int value) {
		this.loadOnStartup = value;
	}

	/**
	 * 返回这个Servlet类名
	 * @return 类名字符串
	 */
	@Override
	public String getServletClass() {
		if(servletClass == null) {
			if(servlet != null)
				return servlet.getClass().getName();
			return null;
		}
		return servletClass;
	}

	/**
	 * 设置Servlet类名，由web.xml文件的servlet-class制定
	 * @param servletClass Servlet类名
	 */
	@Override
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * 这个Servlet目前可用吗
	 * @return true表示不可用
	 */
	@Override
	public boolean isUnavailable() {
		if(availableTime == 0)
			return false;
		long time = System.currentTimeMillis();
		if(time < availableTime)
			return true;
		return false;
	}

	/**
	 * 添加Servlet初始化参数，此参数应由web.xml文件init-parameter指定
	 * @param name 参数名
	 * @param value 参数值
	 */
	@Override
	public void addInitParameter(String name, String value) {
		parameterMap.put(name, value);
	}

	/**
	 * 根据参数名获取参数值，此参数应由web.xml文件init-parameter指定
	 * @param name
	 * @return 参数值
	 */
	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	/**
	 * @return 包含所有参数名的字符串数组
	 */
	@Override
	public String[] getInitParameters() {
		String[] parameters = new String[parameterMap.size()];
		int i = 0;
		for(String parameter : parameterMap.keySet()) {
			parameters[i++] = parameter;
		}
		return parameters;
	}

	/**
	 * 根据参数名移除参数值
	 * @param name 参数名
	 */
	@Override
	public void removeInitParameter(String name) {
		parameterMap.remove(name);
	}

	/**
	 * 复制一个Servlet实例
	 * @return 新复制的Servlet
	 * @throws ServletException
	 */
	@Override
	public Servlet allocate() throws ServletException {
		if(servlet == null)
			return null;
		try {
			return servlet.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 初始化一个Servlet实例
	 */
	@Override
	public void load() throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * 卸载当前Wrapper中所有的Servlet
	 * @throws ServletException
	 */
	@Override
	public void unload() throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * 添加Servlet对象
	 * @param servlet
	 */
	@Override
	public void addServlet(Servlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * @return 当前Servlet实例
	 */
	@Override
	public Servlet getServlet() {
		return servlet;
	}

	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException {
		throw new IllegalArgumentException("Wrapper not support to add child container");
	}

	@Override
	protected void initInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destoryInternal() throws Exception {
		// TODO Auto-generated method stub

	}
}
