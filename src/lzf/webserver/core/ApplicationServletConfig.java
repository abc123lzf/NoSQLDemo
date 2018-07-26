package lzf.webserver.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��21�� ����3:14:36
* @Description ServletConfig�࣬ÿ��Servletʵ��Ӧ���ж�Ӧ��ServletConfig��
*/
public final class ApplicationServletConfig implements ServletConfig {
	
	public static final String STATIC = "static";
	public static final String SERVLET = "servlet";
	public static final String JSP = "jsp";
	
	//������Wrapper����
	private final Wrapper wrapper;
	
	//Servlet���ƣ���web.xml�ļ���servlet-name����
	String servletName = null;
	
	//Servlet Class����
	String servletClass = null;
	
	String servletType;
	
	final List<String> urlPatterns = new ArrayList<>(1); 
	
	final Map<String, String> parameterMap = new LinkedHashMap<>();
	
	ApplicationServletConfig(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * @return Servlet���ƣ���web.xml�ļ���ע���servlet-name����
	 */
	@Override
	public String getServletName() {
		return servletName;
	}

	/**
	 * @return ��webӦ�ö�Ӧ��ServletContext����
	 */
	@Override
	public ServletContext getServletContext() {
		return ((Context) (wrapper.getParentContainer())).getServletContext();
	}
	
	/**
	 * @param name ��ʼ��������
	 * @return ����ֵ
	 */
	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	/**
	 * @return ��ʼ�������������ϵ�����
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * @return ��Servlet����
	 */
	String getServletClass() {
		return servletClass;
	}

	/**
	 * @param servletClass Servlet��������web.xml�ļ���ע���servlet-class����
	 */
	void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * @param urlPattern ��Servlet��URLƥ�����
	 */
	void addUrlPatern(String... urlPattern) {
		
		for(String url : urlPattern) {
			if(urlPatterns.contains(url))
				continue;
			
			urlPatterns.add(url);
		}
	}
	
	@Override
	public String toString() {
		return "ApplicationServletConfig [servletName=" + servletName + ", servletClass=" + servletClass + "]";
	}
}
