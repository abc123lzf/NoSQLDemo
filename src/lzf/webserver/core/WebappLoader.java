package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.jasper.JspC;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.Loader;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.startup.ServerConstant;
import lzf.webserver.util.StringManager;
import lzf.webserver.util.XMLUtil;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月21日 下午4:30:10
 * @Description web应用载入器，包括XML文件解析，静态资源文件的读取，类加载实现
 */
public final class WebappLoader extends LifecycleBase implements Loader {

	private static final StringManager sm = StringManager.getManager(WebappClassLoader.class);
	
	private static final Log log = LogFactory.getLog(WebappLoader.class);
	
	public static final String DEFAULT_JSP_PACKAGE = "lzf.jasper";

	// 该Web加载器所属的Context容器
	private Context context;
	

	//当需要热替换时，需要重新新建一个WebappClassLoader对象并替换旧的ClassLoader
	private volatile WebappClassLoader classLoader = null;
	
	//JSP类加载器，父类加载器应为本web加载器所属的WebappClassLoader
	private volatile JspClassLoader jspClassLoader = null;
	
	//支持热替换吗
	private boolean reloadable = false;

	public WebappLoader(Context context) {
		this.context = context;
	}

	/**
	 * @return 该web应用的专用类加载器
	 */
	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	@Override
	public ClassLoader getJspClassLoader() {
		return jspClassLoader;
	}

	/**
	 * @return 该web加载器所属的Context对象(一一对应关系)
	 */
	@Override
	public Context getContext() {
		return context;
	}

	/**
	 * @param 该web加载器所属的Context容器
	 * @throws LifecycleException 当该加载器已经启动后调用此方法
	 */
	@Override
	public void setContext(Context context) throws LifecycleException {
		
		if (context == null)
			return;

		if (getLifecycleState().isAvailable()) {
			throw new LifecycleException(sm.getString("WebappLoader.setContext.e0", context.getName()));
		}

		synchronized (this) {
			this.context = context;
			setReloadable(context.getReloadable());
		}
	}

	@Override
	public boolean getReloadable() {
		return reloadable;
	}

	@Override
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	@Override
	protected void initInternal() throws Exception {
		
		classLoader = new WebappClassLoader(WebappClassLoader.class.getClassLoader(), context.getPath());
		jspClassLoader = new JspClassLoader(classLoader, ServerConstant.getConstant().getJspWorkPath(context));
		
		resourceLoad(context.getPath());
		//System.out.println("NAME");
		compileJspFile();
	}

	@Override
	protected void startInternal() throws Exception {

	}

	@Override
	protected void stopInternal() throws Exception {

	}

	@Override
	protected void destoryInternal() throws Exception {

	}

	/**
	 * @param path 单个Web应用主目录，该方法会尝试载入里面所有的文件
	 */
	private void resourceLoad(File file) {
		
		if (file.exists()) {
			File[] files = file.listFiles();
			
			if (files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					
					if (file2.isDirectory()) {
						if(file2.getName().equals("WEB-INF")) {
							loadWebXml();
							continue;
						} else if(file2.getName().equals("META-INF")) {
							continue;
						}
						
						resourceLoad(file2);
						
					} else {
						
						byte[] b = loadFile(file2);
						if(b == null)
							return;
						
						String fileName = file2.getName();
						
						if(!(fileName.endsWith(".class") || fileName.endsWith(".jsp"))) {
							context.addChildContainer(StandardWrapper.getDefaultWrapper(context, file2, b));
							
						} else if(fileName.endsWith(".jsp")) {
							context.addChildContainer(StandardWrapper.getJspWrapper(context, file2));
						}
					}
				}
			}
		}
	}
	
	private void compileJspFile() {
		
		//编译该web应用下所有的jsp文件 
		File jspWork = ServerConstant.getConstant().getJspWorkPath(context);
				
		JspC jspc = new JspC();
				
		jspc.setUriroot(context.getPath().getAbsolutePath());
		jspc.setOutputDir(jspWork.getAbsolutePath());
				
		jspc.setPackage(DEFAULT_JSP_PACKAGE);
		jspc.setCompile(true);
				
		jspc.execute();
	}
	
	/**
	 * @param file 需要载入的资源文件File对象
	 * @return 资源文件二进制数据
	 */
	private byte[] loadFile(File file) {
		
		try {
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(file);
			
			byte[] b = new byte[(int) file.length()];
			fis.read(b);
			
			return b;
			
		} catch (FileNotFoundException e) {
			log.error(sm.getString("WebappLoader.loadFile.e0", file.getAbsolutePath()), e);
		} catch (IOException e) {
			log.error(sm.getString("WebappLoader.loadFile.e1", file.getAbsolutePath()) , e);
		}
		
		return null;
	}
	
	/**
	 * 加载web.xml文件
	 * @param path web.xml文件路径
	 * @return 是否加载成功
	 * @throws DocumentException web.xml文件不符合规范
	 */
	private boolean loadWebXml() {	
		
		File path = new File(context.getPath(), "WEB-INF" + File.separator + "web.xml");
		if(!path.exists())
			return false;
		
		Element root = null;
		try {
			root = XMLUtil.getXMLRoot(path);
		} catch (DocumentException e) {
			log.error(sm.getString("WebappLoader.loadWebXml.e0", context.getName()), e);
			return false;
		}
		
		/*
		Element displayRoot = root.element("display-name");
		if(displayRoot != null){
			String displayName = displayRoot.getStringValue();
		}*/
		
		for(Element contextParam : root.elements("context-param")) {
			
			String paramName = contextParam.element("param-name").getText();
			String paramValue = contextParam.element("param-value").getText();
			
			context.getServletContext().setInitParameter(paramName, paramValue);
		}
		
		
		for(Element filter : root.elements("filter")) {
			
			String filterName = filter.element("filter-name").getText();
			String filterClass = filter.element("filter-class").getText();
			
			ApplicationFilterConfig filterConfig = new ApplicationFilterConfig(context, filterName, filterClass);
			
			for(Element initParam : filter.elements("init-param")) {
				
				String name = initParam.element("param-name").getText();
				String value = initParam.element("param-value").getText();
				
				filterConfig.setInitParameter(name, value);
			}
			
			context.getFilterChain().addFilter(filterConfig);
		}
		
		for(Element filterMapping : root.elements("filter-mapping")) {
			
			String filterName = filterMapping.element("filter-name").getText();
			String urlPattern = filterMapping.element("url-pattern").getText();
			
			ApplicationFilterConfig filterConfig = context.getFilterChain().getFilterConfig(filterName);
			
			if(filterConfig != null) {
				
				if(context.getName().equals("ROOT"))
					filterConfig.addUrlPattern(urlPattern);
				else
					filterConfig.addUrlPattern("/" + context.getName() + urlPattern);
					
			} else {
				log.warn(sm.getString("WebappLoader.loadWebXml.w0", context.getName(), filterName));
			}
		}
		
		//Servlet配置项解析
		
		Map<String, String> servletMap = new LinkedHashMap<>();
		Map<String, Map<String, String>> initParamMap = new LinkedHashMap<>();
		
		for(Element servlet : root.elements("servlet")) {
			
			String servletName = servlet.element("servlet-name").getText();
			String servletClass = servlet.element("servlet-class").getText();
			
			List<Element> initParams = servlet.elements("init-param");
			
			if(initParams != null) {
				
				Map<String, String> map = new LinkedHashMap<>();
				
				for(Element initParam : servlet.elements("init-param")) {
					map.put(initParam.element("param-name").getText(), initParam.element("param-value").getText());
				}
				
				initParamMap.put(servletName, map);
			}
			
			servletMap.put(servletName, servletClass);
		}
		
		for(Element servletMapping : root.elements("servlet-mapping")) {
			
			String servletName = servletMapping.element("servlet-name").getText();
			String uriPattern = servletMapping.element("url-pattern").getText();
			
			if(servletMap.containsKey(servletName)) {
				
				Map<String, String> initParams = null;
				
				if(initParamMap.containsKey(servletName)) {
					initParams = initParamMap.get(servletName);
				}
				
				context.addChildContainer(StandardWrapper.getDynamicWrapper(context, servletName
						, servletMap.get(servletName), uriPattern, initParams));
			} else {
				log.warn(sm.getString("WebappLoader.loadWebXml.w1", context.getName(), servletName));
			}
		}
		
		//Session-Config配置项解析
		
		Element sessionConfig = root.element("session-config");
		
		if(sessionConfig != null) {	
			int sessionTimeOut = Integer.valueOf(sessionConfig.element("session-timeout").getText());
			context.setSessionTimeout(sessionTimeOut);
		}
		
		Element welcomeFileRoot = root.element("welcome-file-list");
		
		if(welcomeFileRoot != null) {
		
			for(Element welcomeFile : welcomeFileRoot.elements("welcome-file")) {
				String weclomeFileName = welcomeFile.getText();
				context.addWelcomeFile(weclomeFileName);
			}
			
		}
		
		Element listenerRoot = root.element("listener");
		
		if(listenerRoot != null) {
			
			for(Element listener : listenerRoot.elements("listener-class")) {
				String listenerClass = listener.getText();
				context.getListenerContainer().addListenerClass(listenerClass);
			}
		}
		
		return true;
	}
}
