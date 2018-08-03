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
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月15日 下午21:45:03
 * @Description HTTP请求抽象类，保存最基本的Request请求信息
 */
public abstract class RequestBase implements HttpServletRequest {
	
	private static final Log log = LogFactory.getLog(RequestBase.class);

	protected static final StringManager sm = StringManager.getManager(RequestBase.class);
	
	//请求行中的请求方法
	protected String method;
	
	//请求行中的请求URI
	protected String requestUrl;
	
	//请求HTTP版本
	protected String protocol;
	
	// 请求头Map
	protected final Map<String, String> headerMap = new ConcurrentHashMap<>();
	
	// GET请求参数Map
	protected final Map<String, String[]> parameterMap = new LinkedHashMap<>();

	protected final List<Locale> localeList = new ArrayList<>(0);
	
	//从请求头中提取的Cookie字段并封装的Cookie数组
	protected Cookie[] cookies = new Cookie[0];
	
	//客户端IP地址
	protected String remoteAddr = null;
	
	//客户端主机名，如果没有主机名默认为IP地址
	protected String remoteHost = null;
	
	//客户端发送请求的端口号
	protected int remotePort = 0;
	
	//请求体Reader
	protected BufferedReader contentReader = null;
	
	protected ServletInputStream sis = null;
	
	protected String characterEncoding = "UTF-8";
	
	//该请求包含的文件上传信息，只有映射的Servlet有MultipartConfig注解时不为null
	protected MultiPartFormData multiData = null;
	
	
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
		return getHeader("Content-Type");
	}
	
	@Override
	public final String getCharacterEncoding() {
		return characterEncoding;
	}
	
	/**
	 * 返回请求体Reader流
	 * @return 包含请求体内容的BufferedReader
	 */
	@Override
	public final BufferedReader getReader() throws IOException {
		return contentReader;
	}


	/**
	 * 获取请求体输入流
	 * @return 请求体输入流
	 */
	@Override
	public final ServletInputStream getInputStream() throws IOException {
		return sis;
	}
	
	/**
	 * 返回HTTP协议版本
	 * @return HTTP/1.1或HTTP/1.0
	 */
	@Override
	public final String getProtocol() {
		return protocol;
	}

	/**
	 * 返回HTTP请求方法
	 * @return GET/POST/DELETE等
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
	 * 获取请求协议，暂不支持HTTPS
	 * @return "http"
	 */
	@Override
	public final String getScheme() {
		return "http";
	}

	/**
	 * 获取主机名，不包括端口号 如：localhost或www.baidu.com
	 * @return 主机名字符串
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
	 * @return (int)端口号
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
	 * 从HTTP请求头的Accpet-Language字段中提取信息并组装成Locale集合
	 * @return Locale集合中第一个Locale对象
	 */
	@Override
	public Locale getLocale() {
		
		if (localeList.size() == 0) {
			getLocalesByString();
		}
		return localeList.get(0);
	}

	/**
	 * 从HTTP请求头的Accpet-Language字段中提取信息并组装成Locale集合
	 * @return Locale对象集合的Enumeration迭代器
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
	 * 根据请求头属性名获取值并转换为int类型，如果不可转换或不存在则会抛出异常
	 * @param name HTTP请求头属性名
	 * @return 转换后的int值
	 */
	@Override
	public final int getIntHeader(String name) {
		return Integer.valueOf(headerMap.get(name));
	}

	/**
	 * 从HTTP请求中获取URI
	 * 例如/index.jsp?id=1即为/index.jsp
	 * @return URI字符串
	 */
	@Override
	public final String getRequestURI() {
		int index = requestUrl.indexOf('?');
		if(index == -1)
			return requestUrl;
		return requestUrl.substring(0, index);
	}

	/**
	 * 获取完整的URL路径，包括http://
	 * @return URL的StringBuffer类
	 */
	@Override
	public final StringBuffer getRequestURL() {
		StringBuffer buf = new StringBuffer(getScheme() + "://" + getServerName() + requestUrl);
		return buf;
	}

	/**
	 * 根据HTTP请求头属性名获得对应的值
	 * @param name HTTP请求头属性名
	 * @return 对应的值
	 */
	@Override
	public final String getHeader(String name) {
		return headerMap.get(name.toLowerCase());
	}

	/**
	 * 根据HTTP请求头属性名获得对应的值，有些请求头包含多值并以;分隔
	 */
	@Override
	public final Enumeration<String> getHeaders(String name) {
		
		String head = getHeader(name);
		String[] values = head.split("; ");
		
		return new IteratorEnumeration<String>(Arrays.asList(values).iterator());
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
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL参数编码错误", e);
			}
		if(parameterMap.get(name) == null)
			return null;
		return parameterMap.get(name)[0];
	}

	/**
	 * 返回参数键的迭代器
	 */
	@Override
	public final Enumeration<String> getParameterNames() {
		
		if(parameterMap.size() == 0) {
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL参数编码错误", e);
			}
		}
		
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * 获取根据键获取参数值
	 */
	@Override
	public final String[] getParameterValues(String name) {
		
		if(parameterMap.size() == 0)
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL参数编码错误", e);
			}
		return parameterMap.get(name);
	}

	/**
	 * 获取Parameter参数Map
	 */
	@Override
	public final Map<String, String[]> getParameterMap() {
		
		if(parameterMap.size() == 0)
			try {
				docodeParameter();
			} catch (UnsupportedEncodingException e) {
				log.error("URL参数编码错误", e);
			}
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
		
		//POST方法提交的表单
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
				//TODO JSON读取
			}
		}
	}
	
	/**
	 * 从HTTP请求中获取Cookie
	 * @return HTTP请求中所有Cookie组成的数组
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
	 * @return 客户端端口号
	 */
	@Override
	public final int getRemotePort() {
		return remotePort;
	}
	
	/**
	 * 用于接收multipart/form-data文件上传请求类
	 * 单个文件对应一个Part对象
	 */
	protected static final class MultiPartFormData implements Part {

		private final RequestBase request;
		//文件数据
		private byte[] data;	
		//文件类型
		protected String contentType;
		//HTML表单中的name属性
		protected String name;
		//文件名
		protected String fileName;
		
		private String contentDisposition;
		
		protected MultiPartFormData(RequestBase request) throws IOException, ServletException {
			this.request = request;
			processData();
		}
		
		/**
		 * 读取单个文件数据
		 * @throws IOException
		 * @throws ServletException 请求体格式不符合规范
		 */
		private void processData() throws IOException, ServletException {
			
			Enumeration<String> en = request.getHeaders("Content-Type");
			en.nextElement();
			String boundary = en.nextElement();
			
			BufferedReader br = request.getReader();
			if(!br.readLine().equals(boundary))
				throw new ServletException("");
			
			//处理请求体中文件请求头(包含文件名等信息)
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
			//读取文件二进制数据
			while((buf = br.readLine()) != null) {
				if(buf.equals(boundary))
					break;	
				sb.append(buf);
			}
			
			data = sb.toString().getBytes();
		}
		
		/**
		 * 读取请求体中的请求头
		 * @param name 键名
		 * @param val 键值
		 * @throws ServletException 格式有误
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
		 * @return 该文件对象的输入流
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
		 * @return 文件的名称
		 */
		@Override
		public String getSubmittedFileName() {
			return this.fileName;
		}

		/**
		 * @return 文件字节数
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
