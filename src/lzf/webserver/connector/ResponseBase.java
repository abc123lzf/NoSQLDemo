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
* @author ���ӷ�����޸�JDKԴ��
* @version 1.0
* @date 2018��7��17�� ����9:35:23
* @Description ��˵��
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
	 * ��ȡ�ֽ������
	 * @param ServletOutputStream ʵ��
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return sos;
	}

	/**
	 * ��ȡ�������ַ�д����
	 * @param PrintWriterʵ��
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
	 * ���Cookie����
	 * @param Cookie����ʵ��
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
	 * ��Cookie����ת����HTTPЭ���׼�ַ�����ʽ
	 * @param cookie Cookie����
	 * @return ��׼Set-Cookie�ַ���
	 */
	private String cookieToString(Cookie cookie) {
		String name = cookie.getName();
		String val = cookie.getValue();
		StringBuilder sb = new StringBuilder(name + "=" + val);
		//ת��Ϊ��׼��HTTP��ʽ��GMTʱ��
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
	 * �����й����ڵ���Ӧͷ���������Ӧͷ�Ѿ������򸲸�ԭ�ȵ�ֵ
	 * @param name ��Ӧͷ��
	 * @param date ʱ���
	 */
	@Override
	public final void setDateHeader(String name, long date) {
		headerMap.put(name, HTTP_DATE_FORMAT.format(date));
	}

	/**
	 * ����й����ڵ���Ӧͷ���������Ӧͷ�Ѿ��������������
	 * @param name ��Ӧͷ��
	 * @param date ʱ���
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
	 * �����Ӧͷ��������и���Ӧͷ�򸲸�
	 * @param name ��Ӧͷ��
	 * @param value ��Ӧͷֵ
	 */
	@Override
	public final void setHeader(String name, String value) {
		headerMap.put(name, value);
	}

	/**
	 * �����Ӧͷ��������и���Ӧͷ���������
	 * @param name ��Ӧͷ��
	 * @param value ��Ӧͷֵ
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
	 * ���int������Ӧͷ��������и���Ӧͷ�򸲸�
	 * @param name ��Ӧͷ��
	 * @param value ��Ӧͷֵ
	 */
	@Override
	public final void setIntHeader(String name, int value) {
		headerMap.put(name, String.valueOf(value));
	}

	/**
	 * ���int������Ӧͷ��������и���Ӧͷ���������
	 * @param name ��Ӧͷ��
	 * @param value ��Ӧͷֵ
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
	 * ������Ӧ״̬��
	 * @param sc ״̬�� ��Χ��100~500
	 */
	@Override
	public final void setStatus(int sc) {
		this.status = sc;
	}

	/**
	 * ��ȡ��Ӧ״̬�룬��200��404
	 * @return ��Ӧ״̬��
	 */
	@Override
	public final int getStatus() {
		return status;
	}

	/**
	 * ������Ӧͷ����ȡֵ
	 * @param name ��Ӧͷ��
	 * @return ��Ӧ��ֵ
	 */
	@Override
	public final String getHeader(String name) {
		return headerMap.get(name);
	}

	/**
	 * ���ݼ�����ȡ��������ֵ�ļ��ϣ�����ü��������ֵ(��';'�ֿ�)
	 * @return Collection����
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
	 * ��ȡ����Ӧͷ���м������Ƽ���
	 * @return Collection����
	 */
	@Override
	public final Collection<String> getHeaderNames() {
		return headerMap.keySet();
	}

}
