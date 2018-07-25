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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����5:39:04
* @Description ��С�����������ڱ��浥��Servlet
*/
public class StandardWrapper extends ContainerBase<Context, Void> implements Wrapper {
	
	private static final Log log = LogFactory.getLog(StandardWrapper.class);

	long availableTime = 0L;
	
	int loadOnStartup = 0;
	
	volatile Servlet servlet;
	
	//ServletConfigʵ���࣬�����Servlet�������ơ�����
	volatile ApplicationServletConfig servletConfig = new ApplicationServletConfig(this);
	
	volatile ApplicationServletRegistration servletRegistration = new ApplicationServletRegistration(this);
	
	//�����Wrapper�����������ļ�·��(��ѡ)
	private File path = null;
	
	//URI·��
	private String uriPath = null;
	
	
	StandardWrapper(Context context) {
		super();
		this.parentContainer = context;
	}

	/**
	 * ���ظ�Servlet���õ�ʱ��������Request���󵽴��ʱ��С��������ô��Servlet������
	 * ����404����
	 * @return ʱ���
	 */
	@Override
	public long getAvailable() {
		return availableTime;
	}

	/**
	 * ���ø�Servlet����ʱ�����ֻ��GMTʱ�䳬����ʱ�䣬��Servlet�ſɱ�����
	 * @param available ����ʱ���
	 */
	@Override
	public void setAvailable(long available) {
		this.availableTime = available;
	}

	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @return load-on-startup����ֵ
	 */
	@Override
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @param value web.xml�е�load-on-startup����ֵ
	 */
	@Override
	public void setLoadOnStartup(int value) {
		this.loadOnStartup = value;
	}

	/**
	 * �������Servlet����
	 * @return �����ַ���
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
	 * ����Servlet��������web.xml�ļ���servlet-class�ƶ�
	 * @param servletClass Servlet����
	 */
	@Override
	public void setServletClass(String servletClass) {
		servletConfig.servletClass = servletClass;
	}

	/**
	 * ���ServletĿǰ������
	 * @return true��ʾ������
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
	 * ���Servlet��ʼ���������˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name ������
	 * @param value ����ֵ
	 */
	@Override
	public void addInitParameter(String name, String value) {
		servletConfig.parameterMap.put(name, value);
	}

	/**
	 * ���ݲ�������ȡ����ֵ���˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name
	 * @return ����ֵ
	 */
	@Override
	public String getInitParameter(String name) {
		return servletConfig.parameterMap.get(name);
	}

	/**
	 * @return �������в��������ַ�������
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
	 * ���ݲ������Ƴ�����ֵ
	 * @param name ������
	 */
	@Override
	public void removeInitParameter(String name) {
		servletConfig.parameterMap.remove(name);
	}

	/**
	 * ����һ��Servletʵ��
	 * @return �¸��Ƶ�Servlet
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
	 * ��ʼ��һ��Servletʵ��
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
	 * ж�ص�ǰWrapper�����е�Servlet
	 * @throws ServletException
	 */
	@Override
	public void unload() throws ServletException {
		servlet.destroy();
		servlet = null;
		servletConfig = null;
	}

	/**
	 * ���Servlet����
	 * @param servlet
	 */
	@Override
	public void addServlet(Servlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * @return ��ǰServletʵ��
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
	 * @return ��URI��ӳ���·��
	 */
	@Override
	public String getURIPath() {
		return uriPath;
	}
	
	/**
	 * @param uri ��URI��ӳ���·��
	 */
	@Override
	public void setURIPath(String uri) {
		this.uriPath = uri;
	}
	
	/**
	 * @param servlet Servletʵ��
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
	 * ����һ������Ĭ��Servlet��Wrapper��ʹ��ʱע��Ҫ�ֶ������Context����
	 * @param context Context������
	 * @param path ����Դ�ļ�·��
	 * @param b ����������
	 * @return �����úõ�Wrapperʵ��
	 */
	public static Wrapper getDefaultWrapper(Context context, File path, byte[] b) {
		
		StandardWrapper wrapper = new StandardWrapper(context);
		
		wrapper.setServlet(new DefaultServlet(path.getName(), b));
		wrapper.setPath(path);
		
		//��wrapper��ŵ�·������ʽ:webapps/ROOT/index.html
		String p = path.getPath().replaceAll("\\\\", "/");
		
		if(context.getName().equals("ROOT")) {
			
			//��webӦ����Ŀ¼����ʽ:webapps/ROOT
			String contextPath = context.getPath().getPath().replaceAll("\\\\", "/");
			
			//����Wrapper��URI����Ϊ/index.html
			wrapper.setURIPath(p.replaceAll(contextPath, ""));

		} else {
			
			//���д��webӦ�õ���Ŀ¼����ʽ:webapps
			String webappBaseFolder = ((Host)(context.getParentContainer())).getWebappBaseFolder().getPath();
			
			//����Wrapper��URI����Ϊ/index.html
			wrapper.setURIPath(p.replaceAll(webappBaseFolder, ""));
		}
		
		wrapper.servletConfig.servletName = "Default";
		wrapper.servletConfig.servletClass = "lzf.webserver.servlets.DefaultServlet";
		wrapper.servletConfig.servletType = ApplicationServletConfig.STATIC;
		
		return wrapper;
	}
	
	/**
	 * ����web.xml�����е�servlet���ɶ�Ӧ��Wrapper����
	 * @param context Context����
	 * @param servletName Servlet���ƣ���Ӧweb.xml�е�servlet-name
	 * @param servletClass Servlet��������Ӧweb.xml�е�servlet-class
	 * @param uriPath URIӳ�䣬��Ӧurl-pattern
	 * @return ���úõ�Wrapper
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
