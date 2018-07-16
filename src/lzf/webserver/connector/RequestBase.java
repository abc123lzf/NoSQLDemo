package lzf.webserver.connector;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import lzf.webserver.util.IteratorEnumeration;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月15日 下午21:45:03
 * @Description HTTP请求抽象类，保存最基本的Request请求信息
 */
public abstract class RequestBase implements HttpServletRequest {

	// 请求行
	protected String method;
	protected String requestUrl;
	protected String protocol;
	// 请求头Map
	private final Map<String, String> headerMap = new ConcurrentHashMap<>();
	// GET请求参数Map
	private final Map<String, String[]> parameterMap = new LinkedHashMap<>();

	private final List<Locale> localeList = new ArrayList<>(0);
	
	protected Cookie[] cookies = new Cookie[0];
	
	//客户端信息
	protected String remoteAddr;
	protected String remoteHost;
	protected int remotePort;

	protected void putHeader(String name, String value) {
		headerMap.put(name.toLowerCase(), value);
	}

	/**
	 * 获取HTTP请求体字段长度
	 * @return 字段长度(int)
	 */
	@Override
	public final int getContentLength() {
		return Integer.valueOf(getHeader("Content-Length"));
	}

	/**
	 * 获取HTTP请求体字段长度
	 * 
	 * @return 字段长度(long)
	 */
	@Override
	public final long getContentLengthLong() {
		return Long.valueOf(getHeader("Content-Length"));
	}

	/**
	 * 获取HTTP请求体内容类型 例如text/html application/x-jpg image/jpeg
	 */
	@Override
	public final String getContentType() {
		return getHeader(getHeader("Content-Type"));
	}

	/**
	 * 返回HTTP协议版本
	 * 
	 * @return HTTP/1.1或HTTP/1.0
	 */
	@Override
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * 返回HTTP请求方法
	 * 
	 * @return GET/POST/
	 */
	@Override
	public final String getMethod() {
		return method.toUpperCase();
	}

	/**
	 * 是否是安全的连接(HTTP或HTTPS) 暂不支持HTTPS
	 */
	@Override
	public final boolean isSecure() {
		return false;
	}

	/**
	 * 暂不支持HTTPS
	 */
	@Override
	public final String getScheme() {
		return "http";
	}

	/**
	 * 获取主机名，不包括端口号 如：localhost或www.baidu.com
	 */
	@Override
	public final String getServerName() {
		String host = getHeader("Host");
		int index = host.lastIndexOf(':');
		if (index == -1)
			return host;
		return host.substring(0, index);
	}

	/**
	 * 获取服务器端口号
	 */
	@Override
	public final int getServerPort() {
		String host = getHeader("Host");
		int index = host.lastIndexOf(':');
		if (index == -1)
			return 80;
		return Integer.valueOf(host.substring(index + 1, host.length()));
	}

	/**
	 * 获取Locale集合中第一个Locale对象
	 */
	@Override
	public Locale getLocale() {
		if (localeList.size() == 0) {
			getLocalesByString();
		}
		return localeList.get(0);
	}

	/**
	 * 获取所有的Locale集合
	 */
	@Override
	public Enumeration<Locale> getLocales() {
		if (localeList.size() == 0) {
			getLocalesByString();
		}
		return new IteratorEnumeration<Locale>(localeList.iterator());
	}

	/**
	 * 根据HTTP请求头中的Accpet-Language创建Locale对象并加入localeList集合中
	 * 若请求头Accept-Language字段为空，则创建默认Locale对象并加入集合中
	 */
	private void getLocalesByString() {
		String accpetLanguage = getHeader("Accept-Language");
		if (accpetLanguage == null)
			localeList.add(Locale.getDefault());

		String[] localesStr = accpetLanguage.substring(0, accpetLanguage.indexOf(';')).split(",");
		for (String localStr : localesStr) {
			String[] detail = localStr.split("-");
			switch (detail.length) {
			case 1:
				localeList.add(new Locale(detail[0]));
				break;
			case 2:
				localeList.add(new Locale(detail[0], detail[1]));
				break;
			case 3:
				localeList.add(new Locale(detail[0], detail[1], detail[2]));
				break;
			}
		}
	}

	/**
	 * 根据请求头属性名获取值并转换为int类型 如果不可转换则会抛出NumberFormatException异常
	 */
	@Override
	public final int getIntHeader(String name) {
		return Integer.valueOf(headerMap.get(name));
	}

	/**
	 * 获取uri(位于请求行)
	 */
	@Override
	public final String getRequestURI() {
		int index = requestUrl.indexOf('?');
		String uri = requestUrl.substring(0, index);
		return uri;
	}

	/**
	 * 获取完整的URL路径
	 * 
	 * @return URL的StringBuffer类
	 */
	@Override
	public final StringBuffer getRequestURL() {
		StringBuffer buf = new StringBuffer(getScheme() + "://" + getServerName() + requestUrl);
		return buf;
	}

	/**
	 * 根据HTTP请求头属性名获得对应的值
	 */
	@Override
	public final String getHeader(String name) {
		return headerMap.get(name.toLowerCase());
	}

	/**
	 * 根据HTTP请求头属性名获得对应的值的迭代器(不建议使用该方法)
	 * 请使用getHeader(String name)方法
	 */
	@Override @Deprecated
	public final Enumeration<String> getHeaders(String name) {
		List<String> single = new ArrayList<>(1);
		single.add(headerMap.get(name));
		return new IteratorEnumeration<String>(single.iterator());
	}

	/**
	 * 获取该HTTP请求所有头名的迭代器
	 */
	@Override
	public final Enumeration<String> getHeaderNames() {
		return new IteratorEnumeration<String>(headerMap.keySet().iterator());
	}

	/**
	 * 获取根据键获取参数值，如果该键存在多个值，则返回第一个值
	 */
	@Override
	public final String getParameter(String name) {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap.get(name)[0];
	}

	/**
	 * 返回参数键的迭代器
	 */
	@Override
	public final Enumeration<String> getParameterNames() {
		if(parameterMap.size() == 0)
			docodeParameter();
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * 获取根据键获取参数值
	 */
	@Override
	public final String[] getParameterValues(String name) {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap.get(name);
	}

	/**
	 * 获取Parameter参数Map
	 */
	@Override
	public final Map<String, String[]> getParameterMap() {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap;
	}
	
	/**
	 * 获取请求URL中参数字符串,若没有参数字符串则返回null
	 * 如http://localhost:9090/index.jsp?id=1&user=1则返回id=1&user=1
	 */
	@Override
	public final String getQueryString() {
		int index = requestUrl.indexOf('?');
		if(index == -1)
			return null;
		if(index + 1 == requestUrl.length())
			return null;
		
		return requestUrl.substring(index + 1, requestUrl.length());
	}

	/**
	 * 根据URL提取GET参数并存入parameterMap中
	 */
	private void docodeParameter() {
		String parametersStr = getQueryString();
		if(parametersStr == null)
			return;
		
		String[] parameters = parametersStr.split("&");
		
		if(requestUrl.indexOf('?') + 1 == requestUrl.length())
			return;

		for (String parameter : parameters) {
			String[] entry = parameter.split("=");
			String key = null;
			String value = null;
			if (entry.length == 0) {
				continue;
			} else if (entry.length == 1) {
				key = entry[0];
			} else if (entry.length == 2) {
				key = entry[0];
				value = entry[1];
			} else {
				continue;
			}

			String[] val = parameterMap.get(key);
			if (val == null) {
				val = new String[] { value };
				parameterMap.put(key, val);
			} else {
				String[] newVal = new String[val.length + 1];
				for(int i = 0; i < val.length; i++)
					newVal[i] = val[i];
				newVal[val.length] = value;
				parameterMap.put(key, newVal);
			}
		}
	}
	
	/**
	 * 获取Cookie数组
	 */
	@Override
	public final Cookie[] getCookies() {
		if(cookies.length == 0)
			cookieDecode();
		return cookies;
	}
	
	/**
	 * 根据HTTP报文解析Cookie
	 */
	private void cookieDecode() {
		String cookie = headerMap.get("Cookie");
		if(cookie == null)
			return;
		
		String[] entry = cookie.split("; ");
		Cookie[] cookies = new Cookie[entry.length];
		for(int i = 0; i < entry.length; i++) {
			String[] kv = cookie.split("=");
			cookies[i] = new Cookie(kv[0], kv[1]);
		}
		this.cookies = cookies;
	}
	
	/**
	 * 客户端IP地址
	 * @return 客户端IP地址字符串
	 */
	@Override
	public final String getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * 客户端主机名，一般为IP地址
	 * @return 客户端IP地址或域名
	 */
	@Override
	public final String getRemoteHost() {
		return remoteHost;
	}
	
	/**
	 * 获取发出请求的客户端的端口号
	 */
	@Override
	public final int getRemotePort() {
		return remotePort;
	}
}