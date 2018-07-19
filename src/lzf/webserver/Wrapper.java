package lzf.webserver;

import javax.servlet.Servlet;
import javax.servlet.ServletException;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:39:00
* @Description ��ײ����������浥��JSP��Servletʵ��
*/
public interface Wrapper extends Container {

	/**
	 * ���ظ�Servlet���õ�ʱ��������Request���󵽴��ʱ��С��������ô��Servlet������
	 * ����404����
	 * @return ʱ���
	 */
	public long getAvailable();
	
	/**
	 * ���ø�Servlet����ʱ�����ֻ��GMTʱ�䳬����ʱ�䣬��Servlet�ſɱ�����
	 * @param available ����ʱ���
	 */
	public void setAvailable(long available);
	
	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @return load-on-startup����ֵ
	 */
	public int getLoadOnStartup();
	
	/**
	 * ��������Ƿ���������ʱ��ͼ������servlet
	 * ��ֵΪ0���ߴ���0ʱ����ʾ������Ӧ������ʱ�ͼ������servlet������ֵԽС�������ȼ�Խ�ߣ�
	 * ����һ������ʱ����û��ָ��ʱ����ָʾ�����ڸ�servlet��ѡ��ʱ�ż��أ���web.xml�е�load-on-startup����
	 * @param value web.xml�е�load-on-startup����ֵ
	 */
	public void setLoadOnStartup(int value);
	
	/**
	 * �������Servlet����
	 * @return �����ַ���
	 */
	public String getServletClass();
	
	/**
	 * ����Servlet��������web.xml�ļ���servlet-class�ƶ�
	 * @param servletClass Servlet����
	 */
	public void setServletClass(String servletClass);
	
	/**
	 * ���ServletĿǰ������
	 * @return true��ʾ������
	 */
	public boolean isUnavailable();
	
	/**
	 * ���Servlet��ʼ���������˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name ������
	 * @param value ����ֵ
	 */
	public void addInitParameter(String name, String value);
	
	/**
	 * ���ݲ�������ȡ����ֵ���˲���Ӧ��web.xml�ļ�init-parameterָ��
	 * @param name
	 * @return ����ֵ
	 */
	public String getInitParameter(String name);
	
	/**
	 * @return �������в��������ַ�������
	 */
	public String[] getInitParameters();
	
	/**
	 * ���ݲ������Ƴ�����ֵ
	 * @param name ������
	 */
	public void removeInitParameter(String name);
	
	/**
	 * ����һ��Servletʵ��
	 * @return �¸��Ƶ�Servlet
	 * @throws ServletException
	 */
	public Servlet allocate() throws ServletException;
	
	/*
	 * ��ʼ��һ��Servletʵ��
	 */
	public void load() throws ServletException;
	
	/**
	 * ж�ص�ǰWrapper�����е�Servlet
	 * @throws ServletException
	 */
	public void unload() throws ServletException;
	
	/**
	 * ���Servlet����
	 * @param servlet
	 */
	public void addServlet(Servlet servlet);
	
	/**
	 * @return ��ǰServletʵ��
	 */
	public Servlet getServlet();
	
	/**
	 * @return ���Servlet�ǵ��߳�ģʽ��
	 */
	public boolean isSingleThreadModel();
	
}
