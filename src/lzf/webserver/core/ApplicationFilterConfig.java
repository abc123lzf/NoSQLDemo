package lzf.webserver.core;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import lzf.webserver.Context;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.IteratorEnumeration;
import lzf.webserver.util.StringManager;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��20�� ����1:45:14
 * @Description ApplicationFilterConfig������Filter����������Context(WebӦ��)��filter�����ơ�filter������
 *              web.xml�ļ��У���<filter></filter>���ã�����<filter-name>��ӦFilter�����ƣ�<filter-class>Ϊ��Filterȫ�޶�����
 *              <init-param>��Ӧ�ų�ʼ������
 */
public class ApplicationFilterConfig implements FilterConfig {

	private static final StringManager sm = StringManager.getManager(ApplicationFilterConfig.class);
	
	private static final Log log = LogFactory.getLog(ApplicationFilterConfig.class);

	// ������Context����
	private final Context context;

	// ��Ӧ��Filter����
	private Filter filter = null;

	// Filter����
	private String filterName;

	// ����Filter��ȫ�޶�����
	private String filterClass;
	
	private String[] urlPatterns = new String[0];
	
	ApplicationFilterRegistration filterRegistration = new ApplicationFilterRegistration(this);

	// �����ʼ��������Map
	final Map<String, String> parameters = new LinkedHashMap<>();

	ApplicationFilterConfig(Context context, String filterName, String filterClass) {
		this.context = context;
		this.filterName = filterName;
		this.filterClass = filterClass;
	}

	/**
	 * @return Filter���ƣ���web.xml��<filter-name>��Ӧ�Ĳ���
	 */
	@Override
	public String getFilterName() {
		return filterName;
	}

	/**
	 * @return ��webӦ��������ServletContext����
	 */
	@Override
	public ServletContext getServletContext() {
		return context.getServletContext();
	}

	/**
	 * @param ����������web.xml�ж�Ӧ<init-param>�е�<param-name>�Ĳ���
	 * @return ����ֵ����web.xml�ж�Ӧ<init-param>�е�<param-value>�Ĳ���
	 */
	@Override
	public String getInitParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * @return �������в������ĵ�������������<init-param>�е�<param-name>�Ĳ����ĵ�����
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<>(parameters.keySet().iterator());
	}

	/**
	 * ����URLƥ�������web.xml�ļ���url-pattern����
	 * @return URLƥ���������
	 */
	public String[] getUrlPatterns() {
		return urlPatterns;
	}
	
	/**
	 * @return ��FilterConfig������Context����
	 */
	Context getContext() {
		return context;
	}

	/**
	 * ���ò�����ֵ�ԣ���web.xml�ж�Ӧ<init-param>
	 * 
	 * @param name
	 *            ����������web.xml�ж�Ӧ<init-param>�е�<param-name>�Ĳ���
	 * @param value
	 *            ����ֵ����web.xml�ж�Ӧ<init-param>�е�<param-value>�Ĳ���
	 */
	void setInitParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * �÷���Ӧ����������
	 * @return ��FilterConfig������Filter
	 */
	Filter getFilter() {
		try {
			if (filter == null)
				try {
					filter = (Filter) context.getWebappLoader().getClassLoader().loadClass(filterClass).newInstance();
					filter.init(this);
					return filter;
				} catch (ServletException e) {
					log.error("Filter:" + filterClass + " ��ʼ���쳣", e);
					return null;
				}
			else {
				return filter;
			}
		} catch (InstantiationException e) {
			log.error(sm.getString("ApplicationFilterConfig.getFilter.e0", filterClass), e);
		} catch (IllegalAccessException e) {
			log.error(sm.getString("ApplicationFilterConfig.getFilter.e1", filterClass), e);
		} catch (ClassNotFoundException e) {
			log.error(sm.getString("ApplicationFilterConfig.getFilter.e2", filterClass), e);
		}
		
		return null;
	}

	/**
	 * ����Filter�����ƣ���web.xml�ļ�����<filter-name>����
	 * 
	 * @param filterName
	 *            Filter����
	 */
	void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * @return ��Filter������ȫ�޶���������web.xml�ļ��ж�Ӧ<filter-class>
	 */
	String getFilterClass() {
		return filterClass;
	}
	
	/**
	 * ���URLƥ�������web.xml�ļ���url-pattern����
	 * @param urlPattern URLƥ�����
	 */
	void addUrlPattern(String urlPattern) {
		
		String[] array = Arrays.copyOf(urlPatterns, urlPatterns.length + 1);
		
		array[array.length - 1] = urlPattern;
		
		urlPatterns = array;
	}
	
}
