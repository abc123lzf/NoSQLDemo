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
	
	private String name;
	
	private String domain;
	
	private String path;
	
	private String comment;
	
	private boolean httpOnly;
	
	private boolean secure;
	
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

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	@Override
	public boolean isHttpOnly() {
		return httpOnly;
	}

	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	@Override
	public int getMaxAge() {
		return maxAge;
	}
}
