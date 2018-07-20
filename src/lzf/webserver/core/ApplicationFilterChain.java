package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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
	 * 线程私有对象，表示当前线程所访问的Filters数组下标
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
			Filter filter = filterConfig.getFilter();
			filter.doFilter(request, response, this);
			pos.set(pos.get() + 1);
		} 
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
			this.filters = newFilters;
		} else {
			filters[n + 1] = filterConfig;
		}
		n++;
	}
	
	/**
	 * 释放所有的FilterConfig
	 */
	synchronized void release() {
		for(int i = 0; i < n; i++) {
			filters[i] = null;
		}
		n = 0;
	}
}
