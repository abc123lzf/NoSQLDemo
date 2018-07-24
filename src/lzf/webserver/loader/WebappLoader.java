package lzf.webserver.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import lzf.webserver.Context;
import lzf.webserver.LifecycleException;
import lzf.webserver.Loader;
import lzf.webserver.core.LifecycleBase;
import lzf.webserver.core.StandardWrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.XMLUtil;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��21�� ����4:30:10
 * @Description webӦ��������������XML�ļ���������̬��Դ�ļ��Ķ�ȡ�������ʵ��
 */
@SuppressWarnings("unused")
public class WebappLoader extends LifecycleBase implements Loader {

	public static final Log log = LogFactory.getLog(WebappLoader.class);

	// ��Web������������Context����
	private Context context = null;

	//����Ҫ���滻ʱ����Ҫ�����½�һ��WebappClassLoader�����滻�ɵ�ClassLoader
	private volatile WebappClassLoader classLoader = new WebappClassLoader(WebappClassLoader.class.getClassLoader(), context.getPath());

	private boolean reloadable = false;

	public WebappLoader(Context context) {
		this.context = context;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Context getContext() {
		return context;
	}

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
		resourceLoad(context.getPath());
	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

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
						
						if(file2.getPath().equals("\\WEB-INF\\classes") || file2.getPath().equals("\\WEB-INF\\lib"))
							continue;
						resourceLoad(file2);
						
					} else {
						
						byte[] b = loadFile(file2);
						if(b == null)
							return;
						
						String fileName = file2.getName();
						if(!(fileName.endsWith("class") || fileName.endsWith("jsp"))) {
							context.addChildContainer(StandardWrapper.getDefaultWrapper(context, file2, b));
						}
						
					}
				}
				
			}
		}
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
			log.error("�Ҳ�����Դ�ļ���" + file.getAbsolutePath(), e);
		} catch (IOException e) {
			log.error("�޷�������Դ�ļ���" + file.getAbsolutePath() , e);
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
			log.error("Context: " + context.getName() + " web.xml read error", e);
			return false;
		}
		
		String displayName = root.element("display-name").getStringValue();
		
		for(Element contextParam : root.elements("context-param")) {
			String paramName = contextParam.element("param-name").getStringValue();
			String paramValue = contextParam.element("param-value").getStringValue();
		}
		
		for(Element filter : root.elements("filter")) {
			String filterName = filter.element("filter-name").getStringValue();
			String filterClass = filter.element("filter-class").getStringValue();
		}
		
		for(Element filterMapping : root.elements("filter-mapping")) {
			String filterName = filterMapping.element("filter-name").getStringValue();
			String urlPattern = filterMapping.element("url-pattern").getStringValue();
		}
		
		for(Element servlet : root.elements("servlet")) {
			String servletName = servlet.element("servlet-name").getStringValue();
			String servletClass = servlet.element("servlet-class").getStringValue();
		}
		
		for(Element servletMapping : root.elements("servlet-mapping")) {
			String servletName = servletMapping.element("servlet-name").getStringValue();
			String servletClass = servletMapping.element("servlet-class").getStringValue();
		}
		
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
		}
		
		return true;
	}
	

}
