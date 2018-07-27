package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.jsp.JspFactory;

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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��21�� ����4:30:10
 * @Description webӦ��������������XML�ļ���������̬��Դ�ļ��Ķ�ȡ�������ʵ��
 */
public final class WebappLoader extends LifecycleBase implements Loader {

	private static final StringManager sm = StringManager.getManager(WebappClassLoader.class);
	
	private static final Log log = LogFactory.getLog(WebappLoader.class);
	
	public static final String DEFAULT_JSP_PACKAGE = "lzf.jasper";

	// ��Web������������Context����
	private Context context;

	//����Ҫ���滻ʱ����Ҫ�����½�һ��WebappClassLoader�����滻�ɵ�ClassLoader
	private volatile WebappClassLoader classLoader = null;
	
	//JSP������������������ӦΪ��web������������WebappClassLoader
	private volatile JspClassLoader jspClassLoader = null;
	
	//֧�����滻��
	private boolean reloadable = false;

	public WebappLoader(Context context) {
		this.context = context;
	}

	/**
	 * @return ��webӦ�õ�ר���������
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
	 * @return ��web������������Context����(һһ��Ӧ��ϵ)
	 */
	@Override
	public Context getContext() {
		return context;
	}

	/**
	 * @param ��web������������Context����
	 * @throws LifecycleException ���ü������Ѿ���������ô˷���
	 */
	@Override
	public void setContext(Context context) throws LifecycleException {
		if (context == null)
			return;

		if (getLifecycleState().isAvailable()) {
			throw new LifecycleException("The webAppLoader is running");
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
	 * @param path ����WebӦ����Ŀ¼���÷����᳢�������������е��ļ�
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
		
		
		//�����webӦ�������е�jsp�ļ� 
		File jspWork = ServerConstant.getConstant().getJspWorkPath(context);
		
		JspC jspc = new JspC();
		
		jspc.setUriroot(context.getPath().getAbsolutePath());
		jspc.setOutputDir(jspWork.getAbsolutePath());
		
		jspc.setPackage(DEFAULT_JSP_PACKAGE);
		jspc.setCompile(true);
		
		jspc.execute();
	}
	
	/**
	 * @param file ��Ҫ�������Դ�ļ�File����
	 * @return ��Դ�ļ�����������
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
	 * ����web.xml�ļ�
	 * @param path web.xml�ļ�·��
	 * @return �Ƿ���سɹ�
	 * @throws DocumentException
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
			
			String paramName = contextParam.element("param-name").getStringValue();
			String paramValue = contextParam.element("param-value").getStringValue();
			
			context.getServletContext().setInitParameter(paramName, paramValue);
		}
		
		
		for(Element filter : root.elements("filter")) {
			
			String filterName = filter.element("filter-name").getStringValue();
			String filterClass = filter.element("filter-class").getStringValue();
			
			ApplicationFilterConfig filterConfig = new ApplicationFilterConfig(context, filterName, filterClass);
			
			for(Element initParam : filter.elements("init-param")) {
				
				String name = initParam.element("param-name").getStringValue();
				String value = initParam.element("param-value").getStringValue();
				
				filterConfig.setInitParameter(name, value);
			}
			
			context.getFilterChain().addFilter(filterConfig);
		}
		
		for(Element filterMapping : root.elements("filter-mapping")) {
			
			String filterName = filterMapping.element("filter-name").getStringValue();
			String urlPattern = filterMapping.element("url-pattern").getStringValue();
			
			ApplicationFilterConfig filterConfig = context.getFilterChain().getFilterConfig(filterName);
			
			if(filterConfig != null) {
				filterConfig.addUrlPattern(urlPattern);
			} else {
				log.warn(sm.getString("WebappLoader.loadWebXml.w0", context.getName(), filterName));
			}
		}
		
		Map<String, String> servletMap = new LinkedHashMap<>();
		
		for(Element servlet : root.elements("servlet")) {
			String servletName = servlet.element("servlet-name").getStringValue();
			String servletClass = servlet.element("servlet-class").getStringValue();
			
			servletMap.put(servletName, servletClass);
		}
		
		for(Element servletMapping : root.elements("servlet-mapping")) {
			
			String servletName = servletMapping.element("servlet-name").getStringValue();
			String uriPattern = servletMapping.element("url-pattern").getStringValue();
			
			if(servletMap.containsKey(servletName)) {
				context.addChildContainer(StandardWrapper.getDynamicWrapper(context, servletName
						, servletMap.get(servletName), uriPattern));
			} else {
				log.warn(sm.getString("WebappLoader.loadWebXml.w1", context.getName(), servletName));
			}
		}
		
		/*
		Element sessionConfig = root.element("session-config");
		
		//TODO mime-mapping
		
		for(Element welcomeFile : root.element("welcome-file-list").elements("welcome-file")) {
			String weclomeFileName = welcomeFile.getStringValue();
		}
		
		for(Element taglib : root.elements("taglib")) {
			String taglibUri = taglib.element("taglib-uri").getStringValue();
			String taglibLocation = taglib.element("taglib-location").getStringValue();
		}
		
		for(Element listener : root.element("listener").elements("listener-class")) {
			String listenerClass = listener.getStringValue();
		}*/
		
		return true;
	}
}
