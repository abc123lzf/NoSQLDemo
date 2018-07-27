package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月20日 下午13:39:03
 * @Description FilterChain实现类，保存Filter的过滤器链
 */
public class ApplicationFilterChain implements FilterChain {

	public static final int INCR = 8;
	
	private ApplicationFilterConfig[] filters = new ApplicationFilterConfig[0];
	
	//当前FilterConfig的数量
	private int n = 0;
	
	
	/**
	 * 线程私有变量，表示当前线程所访问的Filters数组下标
	 */
	private ThreadLocal<Integer> pos = new ThreadLocal<Integer>() {
		//初始化数组下标为0
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};
	
	/**
	 * 将请求转发给过滤器链下一个filter
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		
		if(pos.get() < n) {
			
			ApplicationFilterConfig filterConfig = filters[pos.get()];
			pos.set(pos.get() + 1);
			
			Filter filter = filterConfig.getFilter();
			String uri = ((HttpServletRequest)request).getRequestURI();
			
			//System.out.println(uri + " LLLLLLLLLLL" + filterConfig.getUrlPatterns()[0]);
			
			for(String pattern : filterConfig.getUrlPatterns()) {
				
				if(pattern.indexOf('*') == -1) {
					if(uri.equals(pattern)) {
						filter.doFilter(request, response, this);
						break;
					}
						
				} else {
					if(uri.matches(pattern.replace("*", ".*?"))) {
						filter.doFilter(request, response, this);
						break;
					}
				}
			}
		}
		
		pos.set(0);
		return;
	}

	/**
	 * 添加ApplicationFilterConfig
	 * @param filterConfig ApplicationFilterConfig对象
	 */
	synchronized void addFilter(ApplicationFilterConfig filterConfig) {
		
		//检查是否重复添加
		for(ApplicationFilterConfig filter : filters)
			if(filter == filterConfig)
				return;
			
		if(filters.length == n) {
			
			ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[filters.length + INCR];
			
			int i = 0;
			for(ApplicationFilterConfig config : filters) {
				newFilters[i++] = config;
			}
			
			newFilters[i] = filterConfig;
			//System.out.println("i====="+i);
			this.filters = newFilters;
			
		} else {
			filters[n + 1] = filterConfig;
		}
		
		n++;
	}
	
	/**
	 * 根据Filter名称获取FilterConfig对象
	 * @param filterName Filter名称
	 * @return FilterConfig对象
	 */
	ApplicationFilterConfig getFilterConfig(String filterName) {
		
		for(ApplicationFilterConfig filterConfig : filters) {
			if(filterConfig.getFilterName().equals(filterName)) {
				return filterConfig;
			}
		}
		
		return null;
	}
	
	/**
	 * 释放所有的FilterConfig并销毁Filter
	 */
	synchronized void release() {
		
		for(int i = 0; i < n; i++) {
			filters[i].getFilter().destroy();
			filters[i] = null;
		}
		
		n = 0;
	}
}
