package lzf.webserver.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import lzf.webserver.util.IteratorEnumeration;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��15�� ����21:45:03
 * @Description HTTP��������࣬�����������Request������Ϣ
 */
public abstract class RequestBase implements HttpServletRequest {

	// ������
	protected String method;
	protected String requestUrl;
	protected String protocol;
	// ����ͷMap
	private final Map<String, String> headerMap = new ConcurrentHashMap<>();
	// GET�������Map
	private final Map<String, String[]> parameterMap = new LinkedHashMap<>();

	private final List<Locale> localeList = new ArrayList<>(0);
	
	protected Cookie[] cookies = new Cookie[0];
	
	//�ͻ�����Ϣ
	protected String remoteAddr = null;
	protected String remoteHost = null;
	protected int remotePort = 0;
	
	//������Reader
	protected BufferedReader contentReader = null;
	protected ServletInputStream sis = null;
	
	protected String characterEncoding = null;

	protected void putHeader(String name, String value) {
		headerMap.put(name.toLowerCase(), value);
	}

	/**
	 * ��ȡHTTP�������ֶγ���
	 * @return �ֶγ���(int)
	 */
	@Override
	public final int getContentLength() {
		return Integer.valueOf(getHeader("Content-Length"));
	}

	/**
	 * ��ȡHTTP�������ֶγ���
	 * @return �ֶγ���(long)
	 */
	@Override
	public final long getContentLengthLong() {
		return Long.valueOf(getHeader("Content-Length"));
	}

	/**
	 * ��ȡHTTP�������������� ����text/html application/x-jpg image/jpeg
	 */
	@Override
	public final String getContentType() {
		return getHeader("Content-Type");
	}
	
	@Override
	public final String getCharacterEncoding() {
		return getHeader("Content-Type");
	}
	
	/**
	 * ����������Reader��
	 * @return �������������ݵ�BufferedReader
	 */
	@Override
	public final BufferedReader getReader() throws IOException {
		return contentReader;
	}


	/**
	 * ��ȡ������������
	 * @return ������������
	 */
	@Override
	public final ServletInputStream getInputStream() throws IOException {
		return sis;
	}
	
	/**
	 * ����HTTPЭ��汾
	 * @return HTTP/1.1��HTTP/1.0
	 */
	@Override
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * ����HTTP���󷽷�
	 * @return GET/POST/DELETE��
	 */
	@Override
	public final String getMethod() {
		return method.toUpperCase();
	}

	/**
	 * �Ƿ��ǰ�ȫ������(HTTP��HTTPS) �ݲ�֧��HTTPS
	 */
	@Override
	public final boolean isSecure() {
		return false;
	}

	/**
	 * ��ȡ����Э�飬�ݲ�֧��HTTPS
	 * @return "http"
	 */
	@Override
	public final String getScheme() {
		return "http";
	}

	/**
	 * ��ȡ���������������˿ں� �磺localhost��www.baidu.com
	 * @return �������ַ���
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
	 * ��ȡ�������˿ں�
	 * @return (int)�˿ں�
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
	 * ��HTTP����ͷ��Accpet-Language�ֶ�����ȡ��Ϣ����װ��Locale����
	 * @return Locale�����е�һ��Locale����
	 */
	@Override
	public Locale getLocale() {
		if (localeList.size() == 0) {
			getLocalesByString();
		}
		return localeList.get(0);
	}

	/**
	 * ��HTTP����ͷ��Accpet-Language�ֶ�����ȡ��Ϣ����װ��Locale����
	 * @return Locale���󼯺ϵ�Enumeration������
	 */
	@Override
	public Enumeration<Locale> getLocales() {
		if (localeList.size() == 0) {
			getLocalesByString();
		}
		return new IteratorEnumeration<Locale>(localeList.iterator());
	}

	/**
	 * ����HTTP����ͷ�е�Accpet-Language����Locale���󲢼���localeList������
	 * ������ͷAccept-Language�ֶ�Ϊ�գ��򴴽�Ĭ��Locale���󲢼��뼯����
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
	 * ��������ͷ��������ȡֵ��ת��Ϊint���ͣ��������ת���򲻴�������׳��쳣
	 * @param name HTTP����ͷ������
	 * @return ת�����intֵ
	 */
	@Override
	public final int getIntHeader(String name) {
		return Integer.valueOf(headerMap.get(name));
	}

	/**
	 * ��HTTP�����л�ȡURI
	 * ����/index.jsp?id=1��Ϊ/index.jsp
	 * @return URI�ַ���
	 */
	@Override
	public final String getRequestURI() {
		int index = requestUrl.indexOf('?');
		String uri = requestUrl.substring(0, index);
		return uri;
	}

	/**
	 * ��ȡ������URL·��������http://
	 * @return URL��StringBuffer��
	 */
	@Override
	public final StringBuffer getRequestURL() {
		StringBuffer buf = new StringBuffer(getScheme() + "://" + getServerName() + requestUrl);
		return buf;
	}

	/**
	 * ����HTTP����ͷ��������ö�Ӧ��ֵ
	 * @param name HTTP����ͷ������
	 * @return ��Ӧ��ֵ
	 */
	@Override
	public final String getHeader(String name) {
		return headerMap.get(name.toLowerCase());
	}

	/**
	 * ����HTTP����ͷ��������ö�Ӧ��ֵ�ĵ�����(������ʹ�ø÷���)
	 * ��ʹ��getHeader(String name)����
	 */
	@Override @Deprecated
	public final Enumeration<String> getHeaders(String name) {
		List<String> single = new ArrayList<>(1);
		single.add(headerMap.get(name));
		return new IteratorEnumeration<String>(single.iterator());
	}

	/**
	 * ��ȡ��HTTP��������ͷ���ĵ�����
	 */
	@Override
	public final Enumeration<String> getHeaderNames() {
		return new IteratorEnumeration<String>(headerMap.keySet().iterator());
	}

	/**
	 * ��ȡ���ݼ���ȡ����ֵ������ü����ڶ��ֵ���򷵻ص�һ��ֵ
	 */
	@Override
	public final String getParameter(String name) {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap.get(name)[0];
	}

	/**
	 * ���ز������ĵ�����
	 */
	@Override
	public final Enumeration<String> getParameterNames() {
		if(parameterMap.size() == 0)
			docodeParameter();
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * ��ȡ���ݼ���ȡ����ֵ
	 */
	@Override
	public final String[] getParameterValues(String name) {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap.get(name);
	}

	/**
	 * ��ȡParameter����Map
	 */
	@Override
	public final Map<String, String[]> getParameterMap() {
		if(parameterMap.size() == 0)
			docodeParameter();
		return parameterMap;
	}
	
	/**
	 * ��ȡ����URL�в����ַ���,��û�в����ַ����򷵻�null
	 * ��http://localhost:9090/index.jsp?id=1&user=1�򷵻�id=1&user=1
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
	 * ����URL��ȡGET����������parameterMap��
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
	 * ��HTTP�����л�ȡCookie
	 * @return HTTP����������Cookie��ɵ�����
	 */
	@Override
	public final Cookie[] getCookies() {
		if(cookies.length == 0)
			cookieDecode();
		return cookies;
	}
	
	/**
	 * ����HTTP���Ľ���Cookie
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
	 * �ͻ���IP��ַ
	 * @return �ͻ���IP��ַ�ַ���
	 */
	@Override
	public final String getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * �ͻ�����������һ��ΪIP��ַ
	 * @return �ͻ���IP��ַ������
	 */
	@Override
	public final String getRemoteHost() {
		return remoteHost;
	}
	
	/**
	 * ��ȡ��������Ŀͻ��˵Ķ˿ں�
	 * @return �ͻ��˶˿ں�
	 */
	@Override
	public final int getRemotePort() {
		return remotePort;
	}
}