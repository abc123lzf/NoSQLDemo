package lzf.webserver.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import lzf.webserver.Container;
import lzf.webserver.Context;
import lzf.webserver.Wrapper;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��20�� ����5:39:04
* @Description ��С�����������ڱ��浥��Servlet
*/
public class StandardWrapper extends ContainerBase implements Wrapper {
	
	private long availableTime = 0L;
	
	private int loadOnStartup = 0;
	
	private Servlet servlet;
	
	private ApplicationServletConfig servletConfig = new ApplicationServletConfig(this);
	
	
	public StandardWrapper(Context context) {
		super();
		this.parentContainer = context;
	}

	/**
	 * ���ظ�Servlet���õ�ʱ��������Request���󵽴��ʱ��С��������ô��Servlet������
	 * ����404����
	 * @return ʱ���
	 */
	@Override
	public long getAvailable() {
		return availableTime;
	}

	/**
	 * ���ø�Servlet����ʱ�����ֻ��GMTʱ�䳬����ʱ�䣬��Servlet�ſɱ�����
	 * @param available ����ʱ���
	 */
	@Override
	public void setAvailable(long available) {
		this.availableTime = available;
	}

	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @return load-on-startup����ֵ
	 */
	@Override
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @param value web.xml�е�load-on-startup����ֵ
	 */
	@Override
	public void setLoadOnStartup(int value) {
		this.loadOnStartup = value;
	}

	/**
	 * �������Servlet����
	 * @return �����ַ���
	 */
	@Override
	public String getServletClass() {
		if(servletConfig.servletClass == null) {
			if(servlet != null)
				return servlet.getClass().getName();
			return null;
		}
		return servletConfig.servletClass;
	}

	/**
	 * ����Servlet��������web.xml�ļ���servlet-class�ƶ�
	 * @param servletClass Servlet����
	 */
	@Override
	public void setServletClass(String servletClass) {
		servletConfig.servletClass = servletClass;
	}

	/**
	 * ���ServletĿǰ������
	 * @return true��ʾ������
	 */
	@Override
	public boolean isUnavailable() {
		if(availableTime == 0)
			return false;
		long time = System.currentTimeMillis();
		if(time < availableTime)
			return true;
		return false;
	}

	/**
	 * ���Servlet��ʼ���������˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name ������
	 * @param value ����ֵ
	 */
	@Override
	public void addInitParameter(String name, String value) {
		servletConfig.parameterMap.put(name, value);
	}

	/**
	 * ���ݲ�������ȡ����ֵ���˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name
	 * @return ����ֵ
	 */
	@Override
	public String getInitParameter(String name) {
		return servletConfig.parameterMap.get(name);
	}

	/**
	 * @return �������в��������ַ�������
	 */
	@Override
	public String[] getInitParameters() {
		String[] parameters = new String[servletConfig.parameterMap.size()];
		int i = 0;
		for(String parameter : servletConfig.parameterMap.keySet()) {
			parameters[i++] = parameter;
		}
		return parameters;
	}

	/**
	 * ���ݲ������Ƴ�����ֵ
	 * @param name ������
	 */
	@Override
	public void removeInitParameter(String name) {
		servletConfig.parameterMap.remove(name);
	}

	/**
	 * ����һ��Servletʵ��
	 * @return �¸��Ƶ�Servlet
	 * @throws ServletException
	 */
	@Override
	public Servlet allocate() throws ServletException {
		if(servlet == null)
			return null;
		try {
			return servlet.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * ��ʼ��һ��Servletʵ��
	 */
	@Override
	public void load() throws ServletException {
		
	}

	/**
	 * ж�ص�ǰWrapper�����е�Servlet
	 * @throws ServletException
	 */
	@Override
	public void unload() throws ServletException {
		servlet.destroy();
	}

	/**
	 * ���Servlet����
	 * @param servlet
	 */
	@Override
	public void addServlet(Servlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * @return ��ǰServletʵ��
	 */
	@Override
	public Servlet getServlet() {
		return servlet;
	}

	@Override
	protected void addChildContainerCheck(Container container) throws IllegalArgumentException {
		throw new IllegalArgumentException("Wrapper not support to add child container");
	}

	@Override
	protected void initInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void stopInternal() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destoryInternal() throws Exception {
		// TODO Auto-generated method stub

	}
}
