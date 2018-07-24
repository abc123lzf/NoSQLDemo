package lzf.webserver.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRegistration;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月24日 下午10:42:35
* @Description 类说明
*/
public class ApplicationServletRegistration implements ServletRegistration {
	
	private final StandardWrapper wrapper;
	
	ApplicationServletRegistration(StandardWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public String getName() {
		return wrapper.servletConfig.servletName;
	}

	@Override
	public String getClassName() {
		return wrapper.servletConfig.servletClass;
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
	public Set<String> addMapping(String... urlPatterns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRunAsRole() {
		// TODO Auto-generated method stub
		return null;
	}

}
