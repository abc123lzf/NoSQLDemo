package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.mapper.ContextMapper;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����11:54:02
* @Description ����Ӧ������Context��
*/
public class ApplicationServletContext implements ServletContext {
	
	private final Log log = LogFactory.getLog(ApplicationServletContext.class);

	//��ServletContext������web����
	private final StandardContext context;
	
	//Application�����������Map
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	private final Map<String, String> parameterMap = new ConcurrentHashMap<>();
	
	public ApplicationServletContext(StandardContext context) {
		this.context = context;
		context.getListenerContainer().runContextInitializedEvent();
	}
	
	@Override
	public String getContextPath() {
		return context.getPath().getAbsolutePath();
	}

	@Override
	public ServletContext getContext(String uripath) {
		
		//Ϊ��ȷ������WebӦ�ð�ȫ�ԣ�ֻ�ܷ���null
		return null;
	}

	/**
	 * ��Servlet����֧�ֵ�Servlet API�汾������3.1�ķ���3��2.4�ķ���2
	 * @return 3
	 */
	@Override
	public int getMajorVersion() {
		return 3;
	}

	/**
	 * ���ش�servlet����֧�ֵ�Servlet API�Ĵ�Ҫ�汾�� ���ϰ汾2.4������ʵ�ֶ�����ʹ�ô˷�����������4
	 * @return 1
	 */
	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 2;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 1;
	}

	@Override
	public String getMimeType(String file) {
		return null;
	}

	/**
	 * ������Դ����·�����ظ�Ŀ¼�µ�������Դ·������
	 * @param path ����·����������/��ͷ
	 * @return ��Ŀ¼�µ�������Դ·������
	 */
	@Override
	public Set<String> getResourcePaths(String path) {
		
		Set<String> set = new TreeSet<>();
		searchDirectory(new File(context.getPath(), path), set);
		
		return set;
	}
	
	private void searchDirectory(File dir, Set<String> set) {
		
		File[] files = dir.listFiles();
		
		for(File file : files) {
			
			if(file.isDirectory()) {
				searchDirectory(file, set);
			} else {
				set.add(file.getAbsolutePath().replace(context.getPath().getAbsolutePath(), ""));
			}
		}
	}

	/**
	 * @param ��Դ����·������/��ͷ
	 * @return ��ԴURL·��������������򷵻�null
	 */
	@Override
	public URL getResource(String path) throws MalformedURLException {
		
		File file = new File(context.getPath(), path);
		
		if(!file.exists())
			return null;
		
		return file.toURI().toURL();
	}
	

	/**
	 * @param ��Դ����·������/��ͷ
	 * @return ��Դ�ļ�����������������ڸ��ļ��򷵻�null
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		
		File file = new File(context.getPath(), path);
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(file);
			return fis;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * @param path ������Դ·��(��Ϊ��̬Ҳ��Ϊ��̬)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new ApplicationRequestDispatcher(context, path);
	}

	/**
	 * @param name Servlet����
	 * @return RequestDispatcher��������޷��ҵ����Servlet�򷵻�null
	 */
	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		
		Wrapper wrapper = context.getChildContainer(name);
		
		if(wrapper == null)
			return null;
		
		return new ApplicationRequestDispatcher(context, wrapper);
	}

	/**
	 * @param name Servlet����
	 * @return Servlet��������������򷵻�null
	 */
	@Override
	public Servlet getServlet(String name) throws ServletException {
		
		List<Wrapper> list = context.getChildContainers();
		
		for(Wrapper wrapper : list) {
			if(((StandardWrapper)wrapper).servletConfig.servletName.equals(name))
				return ((StandardWrapper)wrapper).getServlet();
		}
		
		return null;
	}

	/**
	 * @return ��Context��������������Servlet�ĵ�����
	 */
	@Override 
	public Enumeration<Servlet> getServlets() {
		
		List<Wrapper> containerList = context.getChildContainers();
		List<Servlet> list = new LinkedList<>();
		
		try {
			for(Wrapper wrapper : containerList) 
				list.add(wrapper.getServlet());
			
			return new IteratorEnumeration<Servlet>(list.iterator());
			
		} catch (ServletException e) {
			
			log.error("", e);
			return null;
		}
	}

	/**
	 * @return ��Context��������Servlet���Ƶĵ�����
	 */
	@Override
	public Enumeration<String> getServletNames() {
		
		List<Wrapper> containerList = context.getChildContainers();
		List<String> list = new LinkedList<>();
		
		for(Wrapper wrapper : containerList) {
			list.add(((StandardWrapper)wrapper).servletConfig.servletName);
		}
		
		return new IteratorEnumeration<String>(list.iterator());
	}

	/**
	 * ��ָ������Ϣд��servlet��־�ļ���ͨ�����¼���־�� 
	 */
	@Override
	public void log(String msg) {
		log.info(msg);
	}

	
	@Override @Deprecated
	public void log(Exception exception, String msg) {
		log.error(msg, exception);
	}

	/**
	 * ������Throwable�쳣�Ľ�������Ϣ�Ͷ�ջ����д��servlet��־�ļ�,ͨ�����¼���־��
	 */
	@Override
	public void log(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	/**
	 * @param path ��Դ����·��
	 * @return ��ʵ·��������http://localhost:9090/path
	 */
	@Override
	public String getRealPath(String path) {
		
		String hostName = context.getParentContainer().getName();
		int port = context.getParentContainer().getParentContainer().getService()
				.getConnectors().get(0).getPort();
		
		String contextName = context.getName();
		String realPath = "http://" + hostName;
		
		if(port != 80) {
			realPath += ":" + port;
		}
		
		if(!contextName.equals("ROOT")) {
			realPath += "/" + contextName;
		}
		
		return realPath += path;
	}

	/**
	 * @return �����������汾��������Ϣ
	 */
	@Override
	public String getServerInfo() {
		return "APlus-Server/1.0";
	}

	/**
	 * ���ݼ�����ȡContext����ĳ�ʼ������
	 * @param name ������
	 * @return ����ֵ
	 */
	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	/**
	 * @return ���г�ʼ�������������ϵĵ�����
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * ��ӳ�ʼ������
	 * @param name ����
	 * @param value ��ֵ
	 * @return �����ӳɹ�����true������ó�ʼ�������Ѿ������򷵻�false
	 */
	@Override
	public boolean setInitParameter(String name, String value) {
		
		if(parameterMap.containsKey(name))
			return false;
		
		parameterMap.put(name, value);
		return true;
	}

	/**
	 * ��ȡApplication�������Attribute����
	 * @param name ������
	 * @return ����ֵ
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * @return Application�������Attribute�����������ϵĵ�����
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<>(attributeMap.keySet().iterator());
	}

	/**
	 * ����Application�������Attribute����������ü��Ѵ��ڻᱻ����
	 * @param name ������
	 * @param object ����ֵ
	 */
	@Override
	public void setAttribute(String name, Object object) {
		attributeMap.put(name, object);
	}

	/**
	 * �Ƴ�Application�������Attribute����
	 * @param name ������
	 */
	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	/**
	 * ���ظ�webӦ�õ�����
	 * @return webӦ������Ĭ��ΪwebӦ�õ��ļ�����
	 */
	@Override
	public String getServletContextName() {
		return context.getName();
	}

	@Override
	public Dynamic addServlet(String servletName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
		
		try {
			return c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
			return null;
		} catch (IllegalAccessException e) {
			log.error("", e);
			return null;
		}
		
	}

	/**
	 * @return ServletRegistrationʵ����
	 */
	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return ((StandardWrapper)context.getChildContainer(servletName)).servletRegistration;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		
		Map<String, ServletRegistration> map = new HashMap<>();
		
		for(Wrapper wrapper : context.getChildContainers()) {
			
			ServletRegistration servletRegistration = ((StandardWrapper)wrapper).servletRegistration;
			map.put(servletRegistration.getName(), servletRegistration);
		}
		
		return map;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
		
		try {
			return c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
		}
		
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return context.getSessionCookieConfig();
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(String className) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends EventListener> void addListener(T t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return ��webӦ�õ�ר���������
	 */
	@Override
	public ClassLoader getClassLoader() {
		return context.getWebappLoader().getClassLoader();
	}

	@Override
	public void declareRoles(String... roleNames) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVirtualServerName() {
		return ((Host)context.getParentContainer()).getName();
	}

}
