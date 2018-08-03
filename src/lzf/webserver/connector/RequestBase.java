package lzf.webserver.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.IteratorEnumeration;
import lzf.webserver.util.StringManager;

/**
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��15�� ����21:45:03
 * @Description HTTP��������࣬�����������Request������Ϣ
 */
public abstract class RequestBase implements HttpServletRequest {
	
	private static final Log log = LogFactory.getLog(RequestBase.class);

	protected static final StringManager sm = StringManager.getManager(RequestBase.class);
	
	//�������е����󷽷�
	protected String method;
	
	//�������е�����URI
	protected String requestUrl;
	
	//����HTTP�汾
	protected String protocol;
	
	// ����ͷMap
	protected final Map<String, String> headerMap = new ConcurrentHashMap<>();
	
	// GET�������Map
	protected final Map<String, String[]> parameterMap = new LinkedHashMap<>();

	protected final List<Locale> localeList = new ArrayList<>(0);
	
	//������ͷ����ȡ��Cookie�ֶβ���װ��Cookie����
	protected Cookie[] cookies = new Cookie[0];
	
	//�ͻ���IP��ַ
	protected String remoteAddr = null;
	
	//�ͻ��������������û��������Ĭ��ΪIP��ַ
	protected String remoteHost = null;
	
	//�ͻ��˷�������Ķ˿ں�
	protected int remotePort = 0;
	
	//������Reader
	protected BufferedReader contentReader = null;
	
	protected ServletInputStream sis = null;
	
	protected String characterEncoding = "UTF-8";
	
	//������������ļ��ϴ���Ϣ��ֻ��ӳ���Servlet��MultipartConfigע��ʱ��Ϊnull
	protected MultiPartFormData multiData = null;
	
	
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
		return characterEncoding;
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
		if(index == -1)
			return requestUrl;
		return requestUrl.substring(0, index);
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
	 * ����HTTP����ͷ��������ö�Ӧ��ֵ����Щ����ͷ������ֵ����;�ָ�
	 */
	@Override
	public final Enumeration<String> getHeaders(String name) {
		
		String head = getHeader(name);
		String[] values = head.split("; ");
		
		return new IteratorEnumeration<String>(Arrays.asList(values).iterator());
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
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL�����������", e);
			}
		if(parameterMap.get(name) == null)
			return null;
		return parameterMap.get(name)[0];
	}

	/**
	 * ���ز������ĵ�����
	 */
	@Override
	public final Enumeration<String> getParameterNames() {
		
		if(parameterMap.size() == 0) {
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL�����������", e);
			}
		}
		
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * ��ȡ���ݼ���ȡ����ֵ
	 */
	@Override
	public final String[] getParameterValues(String name) {
		
		if(parameterMap.size() == 0)
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL�����������", e);
			}
		return parameterMap.get(name);
	}

	/**
	 * ��ȡParameter����Map
	 */
	@Override
	public final Map<String, String[]> getParameterMap() {
		
		if(parameterMap.size() == 0)
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL�����������", e);
			}
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
	 * @throws UnsupportedEncodingException 
	 */
	private void docodeParameter() throws UnsupportedEncodingException {
		
		String parametersStr = getQueryString();
		
		if(parametersStr != null) {
		
			String[] parameters = parametersStr.split("&");
			
			if(requestUrl.indexOf('?') + 1 != requestUrl.length()) {
	
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
						value = URLDecoder.decode(entry[1], characterEncoding);
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
		}
		
		//POST�����ύ�ı�
		if(method.equals("POST")) {
			String contentType = getHeader("Content-Type");
			
			if(contentType.equals("application/x-www-form-urlencoded")) {
				try {
					BufferedReader br = getReader();
					String line = br.readLine();
					
					if(line == null)
						return;
					
					String[] content = line.split("&");
					
					for(String entry : content) {
						String[] kv = entry.split("=");
						
						if(kv[0] == null)
							continue;
						
						if(kv[0] != null) {
							
							String[] s = parameterMap.get(kv[0]);
							String val = kv.length == 2 ? URLDecoder.decode(kv[1], characterEncoding) : null;
							
							if(s == null) {
								parameterMap.put(kv[0], new String[]{val});
							} else {
								String[] ns = Arrays.copyOf(s, s.length + 1);
								ns[s.length] = val;
								parameterMap.put(kv[0], ns);
							}
						}
					}
					
				} catch (IOException e) {
					log.error(e);
				}
			} else if(contentType.equals("application/json")) {
				//TODO JSON��ȡ
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
		
		String cookie = getHeader("Cookie");
		
		if(cookie == null) {
			return;
		}
		
		String[] entry = cookie.split("; ");
		Cookie[] cookies = new Cookie[entry.length];
		
		for(int i = 0; i < cookies.length; i++) {
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
	
	/**
	 * ���ڽ���multipart/form-data�ļ��ϴ�������
	 * �����ļ���Ӧһ��Part����
	 */
	protected static final class MultiPartFormData implements Part {

		private final RequestBase request;
		//�ļ�����
		private byte[] data;	
		//�ļ�����
		protected String contentType;
		//HTML���е�name����
		protected String name;
		//�ļ���
		protected String fileName;
		
		private String contentDisposition;
		
		protected MultiPartFormData(RequestBase request) throws IOException, ServletException {
			this.request = request;
			processData();
		}
		
		/**
		 * ��ȡ�����ļ�����
		 * @throws IOException
		 * @throws ServletException �������ʽ�����Ϲ淶
		 */
		private void processData() throws IOException, ServletException {
			
			Enumeration<String> en = request.getHeaders("Content-Type");
			en.nextElement();
			String boundary = en.nextElement();
			
			BufferedReader br = request.getReader();
			if(!br.readLine().equals(boundary))
				throw new ServletException("");
			
			//�������������ļ�����ͷ(�����ļ�������Ϣ)
			String buf;
			while((buf = br.readLine()) != null) {
				if(buf.equals(" "))
					break;
				
				String[] header = buf.split(": ");
				if(header.length != 2)
					throw new ServletException("");
				
				processHeader(header[0], header[1]);
			}
				
			StringBuilder sb = new StringBuilder("");
			//��ȡ�ļ�����������
			while((buf = br.readLine()) != null) {
				if(buf.equals(boundary))
					break;	
				sb.append(buf);
			}
			
			data = sb.toString().getBytes();
		}
		
		/**
		 * ��ȡ�������е�����ͷ
		 * @param name ����
		 * @param val ��ֵ
		 * @throws ServletException ��ʽ����
		 */
		private void processHeader(String name, String val) throws ServletException {
			
			String[] values = val.split("; ");
			
			name = name.toLowerCase();
			
			if(name.equals("content-disposition")) {
	
				this.contentDisposition = val;
				
				for(String value : values) {
					
					if(value.indexOf('=') == -1)
						continue;
					
					String[] entry = value.split("=");
					if(entry.length != 2)
						throw new ServletException();
					
					entry[1].substring(1, entry[1].length() - 1);
					
					if(entry[0].equals("name"))
						this.name = entry[0];
					else if(entry[0].equals("filename")) 
						this.fileName = entry[0];
				}
			} else if(name.equals("content-type")) {
				this.contentType = val;
			}
		}
		
		/**
		 * @return ���ļ������������
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		@Override
		public String getContentType() {
			return this.contentType;
		}

		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * @return �ļ�������
		 */
		@Override
		public String getSubmittedFileName() {
			return this.fileName;
		}

		/**
		 * @return �ļ��ֽ���
		 */
		@Override
		public long getSize() {
			return data.length;
		}

		@Override
		public void write(String fileName) throws IOException {
			
		}

		@Override
		public void delete() throws IOException {
			
		}

		@Override
		public String getHeader(String name) {
			
			name = name.toLowerCase();
			
			if(name.equals("content-type")) {
				return this.contentType;
			} else if(name.equals("content-disposition")) {
				return this.contentDisposition;
			}
			
			return null;
		}

		@Override
		public Collection<String> getHeaders(String name) {
			
			List<String> list = new ArrayList<>(5);
			Enumeration<String> en = request.getHeaders(name);
			
			while(en.hasMoreElements()) {
				list.add(en.nextElement());
			}
			
			return list;
		}

		@Override
		public Collection<String> getHeaderNames() {
			
			List<String> list = new ArrayList<>(1);
			
			if(this.contentType != null)
				list.add("Content-Type");
			if(this.contentDisposition != null) {
				list.add("Content-Disposition");
			}
			
			return request.headerMap.keySet();
		}
		
	}
}
