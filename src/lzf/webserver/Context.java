package lzf.webserver;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;

import lzf.webserver.core.ApplicationFilterChain;
import lzf.webserver.core.ApplicationListenerContainer;
import lzf.webserver.core.WebappLoader;
import lzf.webserver.mapper.ContextMapper;
import lzf.webserver.session.HttpSessionManager;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:38:44
* @Description WebӦ����������Ӧһ��WebӦ��
*/
public interface Context extends Container<Host, Wrapper> {
	
	//Ĭ��Session ID����
	public static final String DEFAULT_SESSION_NAME = "JSESSIONID";

	/**
	 * ��ȡ��WebӦ�ö�Ӧ��ServletContext����
	 * @return ServletContextʵ��
	 */
	public ServletContext getServletContext();
	
	/**
	 * @return ��WebӦ�ö�Ӧ��Session������
	 */
	public HttpSessionManager getSessionManager();
	
	/**
	 * @return ��webӦ�õ�·����
	 */
	public ContextMapper getMapper();
	
	/**
	 * ��ȡĬ�ϵ�SessionID����(����JSESSIONID)
	 * @return ����Ĭ��SessionID��
	 */
	public String getSessionIdName();
	
	/**
	 * @param name ��webӦ�õ�Ĭ��SessionId��
	 */
	public void setSessionIdName(String name);
	
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
	 * ��ӭҳ���ļ��б���web.xml�ļ���welcome-file-list����
	 * @return ���еĻ�ӭҳ���ļ�����
	 */
	public List<String> getWelcomeFileList();
	
	/**
	 * ��ӻ�ӭҳ��
	 * @param fileName �ļ�����
	 */
	public void addWelcomeFile(String fileName);
	
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
	 * ����SessionID���ɸ�Context��Ӧ��Cookie
	 * @param sessionId SessionID
	 * @return ����Context�����е�SessionCookieConfig��װ��Cookie����
	 */
	public Cookie createSessionCookie(String sessionId);
	
	/**
	 * @return ��Context������webӦ��������
	 */
	public WebappLoader getWebappLoader();
	
	/**
	 * @return ������������͸��ͻ��˱���SessionID��Cookie��Ϣ����
	 */
	public SessionCookieConfig getSessionCookieConfig();
	
	/**
	 * @return �洢��WEBӦ�����õļ���������
	 */
	public ApplicationListenerContainer getListenerContainer();
	
	/**
	 * @return ��webӦ�ö�ӦFilterChain��������
	 */
	public ApplicationFilterChain getFilterChain();
	
	/**
	 * @param encoding ��webӦ����������뷽ʽ
	 */
	public void setRequestCharacterEncoding(String encoding);
	
	/**
	 * @return ��webӦ����������뷽ʽ
	 */
	public String getRequestCharacterEncoding();
	
	/**
	 * @param encoding ��webӦ����Ӧ����뷽ʽ
	 */
	public void setResponseCharacterEncoding(String encoding);
	
	/**
	 * @return ��webӦ����Ӧ����뷽ʽ
	 */
	public String getResponseCharacterEncoding();
}
