package lzf.webserver.core;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import lzf.webserver.Context;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����1:45:14
* @Description ApplicationFilterConfig������Filter����������Context(WebӦ��)��filter�����ơ�filter������
* web.xml�ļ��У���<filter></filter>���ã�����<filter-name>��ӦFilter�����ƣ�<filter-class>Ϊ��Filterȫ�޶�����
* <init-param>��Ӧ�ų�ʼ������
*/
public class ApplicationFilterConfig implements FilterConfig {
	
	//������Context����
	private final Context context;
	
	//��Ӧ��Filter����
	private final Filter filter;
	
	//Filter����
	private String filterName;
	
	//����Filter��ȫ�޶�����
	private final String filterClass;
	
	//�����ʼ��������Map
	private final Map<String, String> parameters = new LinkedHashMap<>();
	
	ApplicationFilterConfig(Context context, Filter filter) {
		this.context = context;
		this.filter = filter;
		this.filterClass = filter.getClass().getName();
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
	 * ���ò�����ֵ�ԣ���web.xml�ж�Ӧ<init-param>
	 * @param name ����������web.xml�ж�Ӧ<init-param>�е�<param-name>�Ĳ���
	 * @param value ����ֵ����web.xml�ж�Ӧ<init-param>�е�<param-value>�Ĳ���
	 */
	public void setInitParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * �÷���Ӧ����������
	 * @return ��FilterConfig������Filter
	 */
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * ����Filter�����ƣ���web.xml�ļ�����<filter-name>����
	 * @param filterName Filter����
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * ��ȡ��Filter������ȫ�޶���������web.xml�ļ��ж�Ӧ<filter-class>
	 * @return
	 */
	public String getFilterClass() {
		return filterClass;
	}
}
