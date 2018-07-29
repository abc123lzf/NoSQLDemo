package lzf.webserver;

import java.io.File;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import lzf.webserver.core.ApplicationServletConfig;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:39:00
* @Description ��ײ����������浥��JSP��Servletʵ��
*/
public interface Wrapper extends Container<Context, Void> {

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
	public Servlet getServlet() throws ServletException;
	
	/**
	 * @return ��URI��ӳ���·��
	 */
	public List<String> getURIPatterns();
	
	/**
	 * @param uri ��URI��ӳ���·��
	 */
	public void addURIPattern(String... uri);
	
	/**
	 * @return ���Wrapper������Ӧ��Դ�Ĵ��·��
	 */
	public File getPath();
	
	/**
	 * @param path ���Wrapper������Ӧ��Դ�Ĵ��·��
	 */
	public void setPath(File path);
	
	/**
	 * @return ��Wrapper������ӦServlet��ServletConfig����
	 */
	public ApplicationServletConfig getServletConfig();
}
