package lzf.webserver.core;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月24日 下午10:49:19
* @Description 类说明
*/
public class ApplicationFilterRegistration implements FilterRegistration {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInitParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getInitParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
			String... servletNames) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<String> getServletNameMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
			String... urlPatterns) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<String> getUrlPatternMappings() {
		// TODO Auto-generated method stub
		return null;
	}

}
