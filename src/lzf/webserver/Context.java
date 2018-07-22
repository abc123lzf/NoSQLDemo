package lzf.webserver;

import java.io.File;

import javax.servlet.ServletContext;

import lzf.webserver.session.HttpSessionManager;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:38:44
* @Description WebӦ����������Ӧһ��WebӦ��
*/
public interface Context extends Container {

	/**
	 * ��ȡ��WebӦ�ö�Ӧ��ServletContext����
	 * @return ServletContextʵ��
	 */
	public ServletContext getServletContext();
	
	/**
	 * ��ȡ��WebӦ�ö�Ӧ��Session������
	 * @return
	 */
	public HttpSessionManager getSessionManager();
	
	/**
	 * ��ȡĬ�ϵ�SessionID����(����JSESSIONID)
	 * @return ����Ĭ��SessionID��
	 */
	public String getSessionIdName();
	
	/**
	 * ��ȡURL����ı����ʽ
	 * @return "UTF-8"
	 */
	public String getEncodedPath();
	
	/**
	 * @return ��webӦ����Ŀ¼
	 */
	public File getPath();
	
	/**
	 * @param path ���ø�webӦ�õ���Ŀ¼
	 */
	public void setPath(File path);
	
	/**
	 * @return ��webӦ��֧������ʱ�ؼ�����
	 */
	public boolean getReloadable();
	
	/**
	 * @param reloadable ��webӦ��֧���ؼ�����
	 */
	public void setReloadable(boolean reloadable);
	
	/**
	 * @return Session����ʱ�������
	 */
	public int getSessionTimeout();
	
	/**
	 * @param timeout Session����ʱ��(��λ������)
	 */
	public void setSessionTimeout(int timeout);
	
	/**
	 * @param webappVersion webapp�汾�ţ���web.xml�ļ�����
	 */
	public void setWebappVersion(String webappVersion);
	
	/**
	 * @return ��ȡ��webapp�汾�ţ���web.xml�ļ�����
	 */
	public String getWebappVersion();
	
	/**
	 * @param encoding
	 */
	public void setRequestCharacterEncoding(String encoding);
	
	public String getRequestCharacterEncoding();
	
	public void setResponseCharacterEncoding(String encoding);
	
	public String getResponseCharacterEncoding();
}
