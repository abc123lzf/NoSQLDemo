package lzf.webserver.core;

import java.io.File;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import lzf.webserver.Context;
import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.servlets.DefaultServlet;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午5:39:04
* @Description 最小的容器，用于保存单个Servlet
*/
public class StandardWrapper extends ContainerBase<Context, Void> implements Wrapper {
	
	private static final Log log = LogFactory.getLog(StandardWrapper.class);

	long availableTime = 0L;
	
	int loadOnStartup = 0;
	
	volatile Servlet servlet;
	
	//ServletConfig实现类，保存该Servlet对象名称、类名
	volatile ApplicationServletConfig servletConfig = new ApplicationServletConfig(this);
	
	volatile ApplicationServletRegistration servletRegistration = new ApplicationServletRegistration(this);
	
	//与这个Wrapper容器关联的文件路径(可选)
	private File path = null;
	
	//URI路径
	private String uriPath = null;
	
	
	StandardWrapper(Context context) {
		super();
		this.parentContainer = context;
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
		if(servletConfig.servletClass == null) {
			if(servlet != null)
				return servlet.getClass().getName();
			return null;
		}
		return servletConfig.servletClass;
	}

	/**
	 * 设置Servlet类名，由web.xml文件的servlet-class制定
	 * @param servletClass Servlet类名
	 */
	@Override
	public void setServletClass(String servletClass) {
		servletConfig.servletClass = servletClass;
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
		servletConfig.parameterMap.put(name, value);
	}

	/**
	 * 根据参数名获取参数值，此参数应由web.xml文件init-parameter指定
	 * @param name
	 * @return 参数值
	 */
	@Override
	public String getInitParameter(String name) {
		return servletConfig.parameterMap.get(name);
	}

	/**
	 * @return 包含所有参数名的字符串数组
	 */
	@Override
	public String[] getInitParameters() {
		String[] parameters = new String[servletConfig.parameterMap.size()];
		int i = 0;
		for(String parameter : servletConfig.parameterMap.keySet()) {
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
		servletConfig.parameterMap.remove(name);
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
		
		if(servlet != null) {
			
			if(servletConfig == null)
				servletConfig = new ApplicationServletConfig(this);
			
			servlet.init(servletConfig);
			return;
		} else if(getServletClass() != null) {
			
			try {
				servlet = (Servlet) (((Context)getParentContainer()).getWebappLoader().getClassLoader()
						.loadClass(servletConfig.getServletClass()).newInstance());
				
				servlet.init(servletConfig);
				
			} catch (InstantiationException e) {
				log.error("", e);
			} catch (IllegalAccessException e) {
				log.error("", e);
			} catch (ClassNotFoundException e) {
				log.error("", e);
			}
			
			return;
		}
		
		throw new ServletException("Servlet class not set.");
	}

	/**
	 * 卸载当前Wrapper中所有的Servlet
	 * @throws ServletException
	 */
	@Override
	public void unload() throws ServletException {
		servlet.destroy();
		servlet = null;
		servletConfig = null;
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
	 * @throws ServletException 
	 */
	@Override
	public Servlet getServlet() throws ServletException {
		if(servlet == null)
			load();
		return servlet;
	}
	
	@Override
	public File getPath() {
		return path;
	}
	
	@Override
	public void setPath(File path) {
		this.path = path;
	}
	
	/**
	 * @return 在URI上映射的路径
	 */
	@Override
	public String getURIPath() {
		return uriPath;
	}
	
	/**
	 * @param uri 在URI上映射的路径
	 */
	@Override
	public void setURIPath(String uri) {
		this.uriPath = uri;
	}
	
	/**
	 * @param servlet Servlet实例
	 */
	void setServlet(Servlet servlet) {
		this.servlet = servlet;
	}


	@Override
	protected void initInternal() throws Exception {
		pipeline.addValve(new StandardWrapperValve());
		load();
	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopInternal() throws Exception {
		unload();
	}

	@Override
	protected void destoryInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		
		return "StandardWrapper [availableTime=" + availableTime + ", loadOnStartup=" + loadOnStartup
				+ ", servletConfig=" + servletConfig.toString() + "]";
	}
	
	/**
	 * 返回一个持有默认Servlet的Wrapper，使用时注意要手动添加至Context容器
	 * @param context Context父容器
	 * @param path 该资源文件路径
	 * @param b 二进制数据
	 * @return 已配置好的Wrapper实例
	 */
	public static Wrapper getDefaultWrapper(Context context, File path, byte[] b) {
		
		StandardWrapper wrapper = new StandardWrapper(context);
		
		wrapper.setServlet(new DefaultServlet(path.getName(), b));
		wrapper.setPath(path);
		
		//该wrapper存放的路径，格式:webapps/ROOT/index.html
		String p = path.getPath().replaceAll("\\\\", "/");
		
		if(context.getName().equals("ROOT")) {
			
			//该web应用主目录，格式:webapps/ROOT
			String contextPath = context.getPath().getPath().replaceAll("\\\\", "/");
			
			//将该Wrapper的URI设置为/index.html
			wrapper.setURIPath(p.replaceAll(contextPath, ""));

		} else {
			
			//所有存放web应用的主目录，格式:webapps
			String webappBaseFolder = ((Host)(context.getParentContainer())).getWebappBaseFolder().getPath();
			
			//将该Wrapper的URI设置为/index.html
			wrapper.setURIPath(p.replaceAll(webappBaseFolder, ""));
		}
		
		wrapper.servletConfig.servletName = "Default";
		wrapper.servletConfig.servletClass = "lzf.webserver.servlets.DefaultServlet";
		wrapper.servletConfig.servletType = ApplicationServletConfig.STATIC;
		
		return wrapper;
	}
	
	/**
	 * 根据web.xml配置中的servlet生成对应的Wrapper容器
	 * @param context Context容器
	 * @param servletName Servlet名称，对应web.xml中的servlet-name
	 * @param servletClass Servlet类名，对应web.xml中的servlet-class
	 * @param uriPath URI映射，对应url-pattern
	 * @return 配置好的Wrapper
	 */
	public static Wrapper getDynamicWrapper(Context context, String servletName, 
			String servletClass, String uriPath) {
		
		StandardWrapper wrapper = new StandardWrapper(context);
		
		wrapper.servletConfig.servletName = servletName;
		wrapper.servletConfig.servletClass = servletClass;
		wrapper.servletConfig.servletType = ApplicationServletConfig.SERVLET;
		
		if(context.getName().equals("ROOT")) {
			
			wrapper.uriPath = uriPath;
			
		} else {
			
			if(uriPath.startsWith("/"))
				wrapper.uriPath = "/" + context.getName() + uriPath;
			else
				wrapper.uriPath = "/" + context.getName() + "/" + uriPath;
		}
		
		wrapper.uriPath = uriPath;
		
		return wrapper;
	}
}
