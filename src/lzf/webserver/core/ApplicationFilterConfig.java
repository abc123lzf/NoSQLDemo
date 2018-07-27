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
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月20日 下午1:45:14
 * @Description ApplicationFilterConfig保存了Filter对象、所属的Context(Web应用)、filter的名称、filter的类名
 *              web.xml文件中，由<filter></filter>配置，其中<filter-name>对应Filter的名称，<filter-class>为该Filter全限定类名
 *              <init-param>对应着初始化参数
 */
public class ApplicationFilterConfig implements FilterConfig {

	private static final StringManager sm = StringManager.getManager(ApplicationFilterConfig.class);
	
	private static final Log log = LogFactory.getLog(ApplicationFilterConfig.class);

	// 所属的Context容器
	private final Context context;

	// 对应的Filter对象
	private Filter filter = null;

	// Filter名称
	private String filterName;

	// 上述Filter的全限定类名
	private String filterClass;
	
	private String[] urlPatterns = new String[0];
	
	ApplicationFilterRegistration filterRegistration = new ApplicationFilterRegistration(this);

	// 保存初始化参数的Map
	final Map<String, String> parameters = new LinkedHashMap<>();

	ApplicationFilterConfig(Context context, String filterName, String filterClass) {
		this.context = context;
		this.filterName = filterName;
		this.filterClass = filterClass;
	}

	/**
	 * @return Filter名称，即web.xml中<filter-name>对应的参数
	 */
	@Override
	public String getFilterName() {
		return filterName;
	}

	/**
	 * @return 该web应用所属的ServletContext对象
	 */
	@Override
	public ServletContext getServletContext() {
		return context.getServletContext();
	}

	/**
	 * @param 参数名，在web.xml中对应<init-param>中的<param-name>的参数
	 * @return 参数值，在web.xml中对应<init-param>中的<param-value>的参数
	 */
	@Override
	public String getInitParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * @return 包含所有参数名的迭代器，即所有<init-param>中的<param-name>的参数的迭代器
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<>(parameters.keySet().iterator());
	}

	/**
	 * 返回URL匹配规则，由web.xml文件的url-pattern决定
	 * @return URL匹配规则数组
	 */
	public String[] getUrlPatterns() {
		return urlPatterns;
	}
	
	/**
	 * @return 该FilterConfig所属的Context容器
	 */
	Context getContext() {
		return context;
	}

	/**
	 * 设置参数键值对，在web.xml中对应<init-param>
	 * 
	 * @param name
	 *            参数名，在web.xml中对应<init-param>中的<param-name>的参数
	 * @param value
	 *            参数值，在web.xml中对应<init-param>中的<param-value>的参数
	 */
	void setInitParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * 该方法应由容器调用
	 * @return 该FilterConfig所属的Filter
	 */
	Filter getFilter() {
		try {
			if (filter == null)
				try {
					filter = (Filter) context.getWebappLoader().getClassLoader().loadClass(filterClass).newInstance();
					filter.init(this);
					return filter;
				} catch (ServletException e) {
					log.error("Filter:" + filterClass + " 初始化异常", e);
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
	 * 设置Filter的名称，在web.xml文件中由<filter-name>设置
	 * 
	 * @param filterName
	 *            Filter名称
	 */
	void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * @return 该Filter所属的全限定类名，在web.xml文件中对应<filter-class>
	 */
	String getFilterClass() {
		return filterClass;
	}
	
	/**
	 * 添加URL匹配规则，由web.xml文件的url-pattern决定
	 * @param urlPattern URL匹配规则
	 */
	void addUrlPattern(String urlPattern) {
		
		String[] array = Arrays.copyOf(urlPatterns, urlPatterns.length + 1);
		
		array[array.length - 1] = urlPattern;
		
		urlPatterns = array;
	}
	
}
