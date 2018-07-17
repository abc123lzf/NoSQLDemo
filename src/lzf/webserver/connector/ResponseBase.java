package lzf.webserver.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
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
	
	protected Map<String, String> headerMap = new LinkedHashMap<>();
	
	protected String characterEncoding = null;
	
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return headerMap.get("Content-Type");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 请求体写入
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		this.characterEncoding = charset;
	}

	@Override
	public void setContentLength(int len) {
		headerMap.put("Content-Length", String.valueOf(len));
	}

	@Override
	public void setContentLengthLong(long length) {
		headerMap.put("Content-Length", String.valueOf(length));
	}

	@Override
	public void setContentType(String type) {
		headerMap.put("Content-Type", type);
	}

	@Override
	public void setBufferSize(int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 添加Cookie对象
	 * @param Cookie对象实例
	 */
	@Override
	public void addCookie(Cookie cookie) {
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
	public boolean containsHeader(String name) {
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

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc, String sm) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
