package lzf.webserver.core;

import javax.servlet.SessionCookieConfig;

import lzf.webserver.Context;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��24�� ����8:38:18
* @Description ������������͸��ͻ��˱���SessionID��Cookie��Ϣ
*/
public class ApplicationSessionCookieConfig implements SessionCookieConfig {
	
	@SuppressWarnings("unused")
	private final Context context;
	
	//Cookie������
	private String name;
	
	//Cookie������
	private String domain;
	
	//Cookie������·������domain��������ͬ���URL
	private String path;
	
	//�Ը�Cookie��������Ϣ
	private String comment;
	
	//���ø�Cookie�ܷ�ͨ��JavaScript����
	private boolean httpOnly;
	
	//������������Cookieֻ�ڰ�ȫ������ʱ�ŷ���(��HTTPS)
	private boolean secure;
	
	//Cookie��expires���ԣ���ʾCookie��ʲôʱ������Ч
	private int maxAge;
	
	
	ApplicationSessionCookieConfig(Context context) {
		
		this.context = context;
		this.name = Context.DEFAULT_SESSION_NAME;
		
		if(context.getName().equals("ROOT"))
			path = "/";
		else
			path = "/" + context.getName();
		
		this.httpOnly = false;
		this.secure = false;
		this.maxAge = -1;
	}
	

	/**
	 * @param name Cookie�ļ���
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Cookie�ļ���
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param domain ��Cookie����������(������)
	 */
	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return ��Cookie����������(������)
	 */
	@Override
	public String getDomain() {
		return domain;
	}

	/**
	 * @param path Cookie������URI
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return Cookie������URI
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * @param comment Cookie��������Ϣ
	 */
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return Cookie��������Ϣ
	 */
	@Override
	public String getComment() {
		return comment;
	}

	/**
	 * @param httpOnly ��Cookie�ܷ�ͨ��JavaScript����
	 */
	@Override
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	/**
	 * @return ��Cookie�ܷ�ͨ��JavaScript����
	 */
	@Override
	public boolean isHttpOnly() {
		return httpOnly;
	}

	/**
	 * @param secure ��Cookieֻ�ڰ�ȫ������(HTTPS)ʱ�ŷ�����
	 */
	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return ��Cookieֻ�ڰ�ȫ������(HTTPS)ʱ�ŷ�����
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Cookie��expires���ԣ���ʾCookie��ʲôʱ������Ч
	 * @param maxAge ��Cookie������ʱ��(��λ������)
	 */
	@Override
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return Cookie��expires���ԣ���ʾCookie��ʲôʱ������Ч(��λ������)
	 */
	@Override
	public int getMaxAge() {
		return maxAge;
	}
	
	@Override
	public String toString() {
		return "ApplicationSessionCookieConfig [context=" + context.getName() + ", name=" + name + ", domain=" + domain
				+ ", path=" + path + ", comment=" + comment + ", httpOnly=" + httpOnly + ", secure=" + secure
				+ ", maxAge=" + maxAge + "]";
	}
	
}
