package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jasper.JspC;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import lzf.webserver.Context;
import lzf.webserver.Host;
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
	
	//默认JSP包名
	public static final String DEFAULT_JSP_PACKAGE = "lzf.jasper";

	// 该Web加载器所属的Context容器
	private Context context;
	
	//当需要热替换时，需要重新新建一个WebappClassLoader对象并替换旧的ClassLoader
	private volatile WebappClassLoader classLoader = null;
	
	//JSP类加载器，父类加载器应为本web加载器所属的WebappClassLoader
	private volatile JspClassLoader jspClassLoader = null;
	
	//支持热替换吗
	private boolean reloadable = false;
	
	//保存资源文件和最后修改时间的映射表
	private Map<File, Long> modifyTimeMap = new ConcurrentHashMap<>();
	
	private ResourceCheckProcess resourceProcess = null;

	WebappLoader(Context context) {
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
		initClassLoader();
		loadWebXml();
		resourceLoad(context.getPath());
		compileJspFile();
	}

	@Override
	protected void startInternal() throws Exception {

	}

	@Override
	protected void stopInternal() throws Exception {
		classLoader = null;
		jspClassLoader = null;
	}

	@Override
	protected void destoryInternal() throws Exception {
		classLoader = null;
		jspClassLoader = null;
	}
	
	/**
	 * 服务器运行期间资源文件监测线程。每个Web应用对应一个此线程。当文件发生修改时，根据实际情况进行热替换
	 * 此线程仅在reloadable为true时启用
	 */
	static final class ResourceCheckProcess implements Runnable {
		
		private final WebappLoader loader;
		private final File contextPath;
		private final Map<File, Long> map;
		private boolean modify = false;
		
		ResourceCheckProcess(WebappLoader loader){
			this.loader = loader;
			this.contextPath = loader.getContext().getPath();
			this.map = loader.modifyTimeMap;
		}

		@Override
		public void run() {
			
			while(loader.getLifecycleState().isAvailable()) {
				checkModify(contextPath);
				if(modify) {
					reloadContext();
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					new Thread(loader.resourceProcess).start();
					return;
				}
			}
		}
		
		/**
		 * 递归检查目录下的文件，并和Map中的文件对象进行比对，若发现修改则将modify标记为true
		 * @param path 搜索的文件夹File对象，外部调用时应传入Context根目录
		 */
		private void checkModify(File path) {
			
			if(!path.exists()) {
				modify = true;
			}
			
			File[] files = path.listFiles();
			
			for(File file : files) {
				if(file.isDirectory()) {
					checkModify(file);
				} else {
					Long ot = file.lastModified();
					Long nt = map.get(file);
					if(nt == null) {
						modify = true;
					}
					
					if(ot != nt) {
						modify = true;
					}
				}
			}
		}
		
		private void reloadContext() {
			
			log.info(sm.getString("WebappLoader.ResourceCheckProcess.i0", contextPath.getName()));
			Host host = loader.getContext().getParentContainer();
			
			synchronized(loader.getContext()) {
				host.removeChildContainer(loader.getContext());
				Context context = StandardContext.createContextByFolder(host, loader.getContext().getPath());
				host.addChildContainer(context);
				try {
					context.init();
					context.start();
				} catch (Exception e) {
					log.error(sm.getString("WebappLoader.ResourceCheckProcess.e0", context.getName()), e);
				}
				
			}
		}
	}
	
	
	/**
	 * 初始化Web类加载器、Jsp类加载器
	 * @throws MalformedURLException
	 */
	private void initClassLoader() throws MalformedURLException {
		
		File lib = new File(context.getPath(), "WEB-INF" + File.separator + "lib");
		File classes = new File(context.getPath(), "WEB-INF" + File.separator + "classes");
		
		File[] files = lib.listFiles();
		
		List<URL> urls = new LinkedList<>();
		urls.add(lib.toURI().toURL());
		urls.add(classes.toURI().toURL());
		
		for(File file : files) {
			urls.add(file.toURI().toURL());
		}
		
		loadClassesPath(urls, classes);
		
		classLoader = new WebappClassLoader(WebappClassLoader.class.getClassLoader(), 
				context.getPath(), urls.toArray(new URL[urls.size()]));
		
		jspClassLoader = new JspClassLoader(classLoader, ServerConstant.getConstant()
				.getJspWorkPath(context));
	}
	
	/**
	 * 读取WEB-INF\classes目录下的class文件
	 * @param urls
	 * @param path
	 * @throws MalformedURLException
	 */
	private void loadClassesPath(final List<URL> urls, File path) throws MalformedURLException {
		
		if(!path.exists())
			return;
		
		File[] files = path.listFiles();
		if(files.length == 0)
			return;
		
		for(File file : files) {
			
			if(file.isDirectory()) {
				urls.add(file.toURI().toURL());
				loadClassesPath(urls, file);
			} else {
				urls.add(file.toURI().toURL());
			}
			
			modifyTimeMap.put(file, file.lastModified());
		}
	}

	/**
	 * @param path 单个Web应用主目录，该方法会尝试载入里面所有的文件
	 */
	private void resourceLoad(File file) {
	
		if(!file.exists())
			return;
		
		File[] files = file.listFiles();
		
		if (files.length == 0) {
			return;
		}
		
		for (File file2 : files) {
			if (file2.isDirectory()) {
				if(file2.getName().equals("META-INF")) {
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
				
				modifyTimeMap.put(file2, file2.lastModified());
			}
		}
	}
	
	/**
	 * 编译该Web应用所有的JSP文件
	 */
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
