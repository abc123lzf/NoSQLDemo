package lzf.webserver.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月24日 下午10:49:19
* @Description 类说明
*/
public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {
	
	private final ApplicationFilterConfig filterConfig;
	
	ApplicationFilterRegistration(ApplicationFilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	@Override
	public String getName() {
		return filterConfig.getFilterName();
	}

	@Override
	public String getClassName() {
		return filterConfig.getFilterName();
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		
		Map<String, String> map = filterConfig.parameters;
		
		if(map.containsKey(name))
			return false;
		
		map.put(name, value);
		return true;
	}

	@Override
	public String getInitParameter(String name) {
		return filterConfig.parameters.get(name);
	}

	/**
	 * @param initParameters 需要设置初始化参数键值对
	 * @return 初始化键值对已经存在的键
	 **/
	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {

		if(initParameters == null) {
			return null;
		}
		
		Set<String> set = new HashSet<>();
		Map<String, String> sourceMap = filterConfig.parameters;
		
		for(Map.Entry<String, String> entry : initParameters.entrySet()) {
			
			String key = entry.getKey();
			
			for(Map.Entry<String, String> entry0 : sourceMap.entrySet()) {
				
				if(entry0.getKey().equals(key)) {
					set.add(entry0.getKey());
					continue;
				}
				
				sourceMap.put(key, entry.getValue());
			}
		}
		
		if(set.isEmpty())
			return null;
		
		return set;
	}

	@Override
	public Map<String, String> getInitParameters() {
		return filterConfig.parameters;
	}

	@Override
	public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
			String... servletNames) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<String> getServletNameMappings() {
		return null;
	}

	@Override
	public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
			String... urlPatterns) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<String> getUrlPatternMappings() {
		return Arrays.asList(filterConfig.getUrlPatterns());
	}

	@Override
	public void setAsyncSupported(boolean isAsyncSupported) {
		
	}

}
