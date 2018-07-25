package lzf.webserver.core;

import javax.servlet.SessionCookieConfig;

import lzf.webserver.Context;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月24日 下午8:38:18
* @Description 保存服务器发送给客户端保存SessionID的Cookie信息
*/
public class ApplicationSessionCookieConfig implements SessionCookieConfig {
	
	@SuppressWarnings("unused")
	private final Context context;
	
	//Cookie键名称
	private String name;
	
	//Cookie的域名
	private String domain;
	
	//Cookie的作用路径，与domain加起来共同组成URL
	private String path;
	
	//对该Cookie的描述信息
	private String comment;
	
	//设置该Cookie能否通过JavaScript访问
	private boolean httpOnly;
	
	//该属性来设置Cookie只在安全的请求时才发送(即HTTPS)
	private boolean secure;
	
	//Cookie的expires属性，表示Cookie在什么时间内有效
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
	 * @param name Cookie的键名
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Cookie的键名
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param domain 该Cookie所属的域名(主机名)
	 */
	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return 该Cookie所属的域名(主机名)
	 */
	@Override
	public String getDomain() {
		return domain;
	}

	/**
	 * @param path Cookie的作用URI
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return Cookie的作用URI
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * @param comment Cookie的描述信息
	 */
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return Cookie的描述信息
	 */
	@Override
	public String getComment() {
		return comment;
	}

	/**
	 * @param httpOnly 该Cookie能否通过JavaScript访问
	 */
	@Override
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	/**
	 * @return 该Cookie能否通过JavaScript访问
	 */
	@Override
	public boolean isHttpOnly() {
		return httpOnly;
	}

	/**
	 * @param secure 该Cookie只在安全的请求(HTTPS)时才发送吗
	 */
	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return 该Cookie只在安全的请求(HTTPS)时才发送吗
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Cookie的expires属性，表示Cookie在什么时间内有效
	 * @param maxAge 该Cookie的生存时间(单位：毫秒)
	 */
	@Override
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return Cookie的expires属性，表示Cookie在什么时间内有效(单位：毫秒)
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
