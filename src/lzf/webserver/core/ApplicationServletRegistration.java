package lzf.webserver.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRegistration;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��24�� ����10:42:35
* @Description ��˵��
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
		
		Map<String, String> parameters = wrapper.servletConfig.parameterMap;
		
		if(parameters.containsKey(name)) {
			return false;
		}
		
		parameters.put(name, value);
		return true;
	}

	/**
	 * �÷���Ĭ�ϴ�ServletConfig�л�ȡ����
	 * @param name ��ʼ��������
	 * @return ����ֵ
	 */
	@Override
	public String getInitParameter(String name) {
		return wrapper.servletConfig.parameterMap.get(name);
	}

	/**
	 * ��Servlet��ӳ�ʼ��������������Ѵ����򲻸���
	 * @param initParameters ��ʼ������Map
	 * @return ��ԭ�еĳ�ʼ���������ͻ�ļ������ϣ���ԭ�е�Map��initParameters�����ڵļ�����,��������ڳ�ͻ�ļ�
	 * 			�򷵻�null
	 */
	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		
		if(initParameters == null) {
			return null;
		}
		
		Set<String> set = new HashSet<>();
		Map<String, String> sourceMap = wrapper.servletConfig.parameterMap;
		
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
		return wrapper.servletConfig.parameterMap;
	}

	@Override
	public Set<String> addMapping(String... urlPatterns) {
		
		if(urlPatterns == null || urlPatterns.length == 0)
			return null;
		
		Set<String> set = new HashSet<>();
		List<String> list = wrapper.servletConfig.urlPatterns;
		
		for(String urlPattern : urlPatterns) {
			
			if(list.contains(urlPattern)) {
				set.add(urlPattern);
				continue;
			}
			
			list.add(urlPattern);
		}
		
		if(set.isEmpty())
			return null;
		return set;
	}

	@Override
	public Collection<String> getMappings() {
		return wrapper.servletConfig.urlPatterns;
	}

	@Override
	public String getRunAsRole() {
		return null;
	}

}
