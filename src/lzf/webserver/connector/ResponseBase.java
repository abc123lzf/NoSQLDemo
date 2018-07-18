package lzf.webserver.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
* @author 李子帆如何修改JDK源码
* @version 1.0
* @date 2018年7月17日 下午9:35:23
* @Description 类说明
*/
public abstract class ResponseBase implements HttpServletResponse {
	
	public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE MMM ddHH:mm:ss 'GMT' yyyy",Locale.US);
	
	protected int status;
	
	protected final Map<String, String> headerMap = new LinkedHashMap<>();
	
	protected String characterEncoding = null;
	
	protected ServletOutputStream sos;
	
	protected PrintWriter pw;
	
	protected ResponseBase() {
		headerMap.put("Content-Type", "text/plain; charset=UTF-8");
		headerMap.put("Server", "LZF-HTTPServer-1.0");
	}
	
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return headerMap.get("Content-Type");
	}

	/**
	 * 获取字节输出流
	 * @param ServletOutputStream 实例
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return sos;
	}

	/**
	 * 获取请求体字符写入流
	 * @param PrintWriter实例
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		return pw;
	}

	@Override
	public final void setCharacterEncoding(String charset) {
		this.characterEncoding = charset;
	}

	@Override
	public final void setContentLength(int len) {
		headerMap.put("Content-Length", String.valueOf(len));
	}

	@Override
	public final void setContentLengthLong(long length) {
		headerMap.put("Content-Length", String.valueOf(length));
	}

	@Override
	public final void setContentType(String type) {
		headerMap.put("Content-Type", type);
	}

	@Override
	public void flushBuffer() throws IOException {
		sos.flush();
	}

	/**
	 * 添加Cookie对象
	 * @param Cookie对象实例
	 */
	@Override
	public final void addCookie(Cookie cookie) {
		if(cookie == null)
			throw new IllegalArgumentException("Cookie is null");
		
		String allCookieStr = headerMap.get("Set-Cookie");
		String cookieStr = cookieToString(cookie);
		if(allCookieStr == null) {
			headerMap.put("Set-Cookie", cookieStr);
		} else {
			headerMap.put("Set-Cookie", allCookieStr + "; " + cookieStr);
		}
	}
	
	/**
	 * 将Cookie对象转化成HTTP协议标准字符串形式
	 * @param cookie Cookie对象
	 * @return 标准Set-Cookie字符串
	 */
	private String cookieToString(Cookie cookie) {
		String name = cookie.getName();
		String val = cookie.getValue();
		StringBuilder sb = new StringBuilder(name + "=" + val);
		//转化为标准的HTTP格式的GMT时间
		int maxAge = cookie.getMaxAge();
		if(maxAge > 0)
			sb.append("; Expires=" + HTTP_DATE_FORMAT.format(System.currentTimeMillis() + cookie.getMaxAge() * 1000));
		String domain = cookie.getDomain();
		if(domain != null)
			sb.append("; Domain=" + domain);
		String path = cookie.getPath();
		if(path != null)
			sb.append("; Path=" + path);
		String comment = cookie.getComment();
		if(comment != null)
			sb.append("; Comment=" + comment);
		Boolean secure = cookie.getSecure();
		if(secure == true) 
			sb.append("; Secure");
		Boolean httpOnly = cookie.isHttpOnly();
		if(httpOnly == true) 
			sb.append("; HttpOnly");
		return sb.toString();
	}

	@Override
	public final boolean containsHeader(String name) {
		return headerMap.containsKey(name);
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 设置有关日期的响应头，如果该响应头已经设置则覆盖原先的值
	 * @param name 响应头名
	 * @param date 时间戳
	 */
	@Override
	public final void setDateHeader(String name, long date) {
		headerMap.put(name, HTTP_DATE_FORMAT.format(date));
	}

	/**
	 * 添加有关日期的响应头，如果该响应头已经设置则往后添加
	 * @param name 响应头名
	 * @param date 时间戳
	 */
	@Override
	public final void addDateHeader(String name, long date) {
		String val = headerMap.get(name);
		if(val != null) {
			val += "; " + HTTP_DATE_FORMAT.format(date);
			headerMap.put(name, val);
			return;
		}
		setDateHeader(name, date);
	}

	/**
	 * 添加响应头，如果已有该响应头则覆盖
	 * @param name 响应头名
	 * @param value 响应头值
	 */
	@Override
	public final void setHeader(String name, String value) {
		headerMap.put(name, value);
	}

	/**
	 * 添加响应头，如果已有该响应头则往后添加
	 * @param name 响应头名
	 * @param value 响应头值
	 */
	@Override
	public final void addHeader(String name, String value) {
		String val = headerMap.get(name);
		if(val != null) {
			val += "; " + value;
			headerMap.put(name, val);
			return;
		}
		setHeader(name, value);
	}

	/**
	 * 添加int类型响应头，如果已有该响应头则覆盖
	 * @param name 响应头名
	 * @param value 响应头值
	 */
	@Override
	public final void setIntHeader(String name, int value) {
		headerMap.put(name, String.valueOf(value));
	}

	/**
	 * 添加int类型响应头，如果已有该响应头则往后添加
	 * @param name 响应头名
	 * @param value 响应头值
	 */
	@Override
	public final void addIntHeader(String name, int value) {
		String val = headerMap.get(name);
		if(val != null) {
			val += "; " + String.valueOf(value);
			headerMap.put(name, val);
			return;
		}
		setIntHeader(name, value);
	}

	/**
	 * 设置响应状态码
	 * @param sc 状态码 范围从100~500
	 */
	@Override
	public final void setStatus(int sc) {
		this.status = sc;
	}

	/**
	 * 获取响应状态码，如200、404
	 * @return 响应状态码
	 */
	@Override
	public final int getStatus() {
		return status;
	}

	/**
	 * 根据响应头名获取值
	 * @param name 响应头名
	 * @return 对应的值
	 */
	@Override
	public final String getHeader(String name) {
		return headerMap.get(name);
	}

	/**
	 * 根据键名获取包含所有值的集合，如果该键包含多个值(用';'分开)
	 * @return Collection集合
	 */
	@Override
	public final Collection<String> getHeaders(String name) {
		if(headerMap.get(name) == null)
			return null;
		String[] headers = headerMap.get(name).split(";");
		List<String> list = new ArrayList<>(headers.length);
		for(String head : headers)
			list.add(head);
		return list;
	}

	/**
	 * 获取该响应头所有键的名称集合
	 * @return Collection集合
	 */
	@Override
	public final Collection<String> getHeaderNames() {
		return headerMap.keySet();
	}

}
