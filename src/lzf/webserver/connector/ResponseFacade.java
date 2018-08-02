package lzf.webserver.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 上午10:31:46
* @Description 类说明
*/
public class ResponseFacade implements HttpServletResponse {

	private final Response response;
	
	public ResponseFacade(Response response) {
		this.response = response;
	}
	
	@Override
	public String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return response.getContentType();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

	@Override
	public void setCharacterEncoding(String charset) {
		response.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		response.setContentLength(len);
	}

	@Override
	public void setContentLengthLong(long length) {
		response.setContentLengthLong(length);
	}

	@Override
	public void setContentType(String type) {
		response.setContentType(type);
	}

	@Override
	public void setBufferSize(int size) {
		response.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return response.getBufferSize();
	}

	@Override
	public void flushBuffer() throws IOException {
		response.flushBuffer();
	}

	@Override
	public void resetBuffer() {
		response.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return response.isCommitted();
	}

	@Override
	public void reset() {
		response.reset();
	}

	@Override
	public void setLocale(Locale loc) {
		response.setLocale(loc);
	}

	@Override
	public Locale getLocale() {
		return response.getLocale();
	}

	@Override
	public void addCookie(Cookie cookie) {
		response.addCookie(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return response.containsHeader(name);
	}

	@Override
	public String encodeURL(String url) {
		return response.encodeURL(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return response.encodeRedirectURL(url);
	}

	@Override @Deprecated
	public String encodeUrl(String url) {
		return response.encodeUrl(url);
	}

	@Override @Deprecated
	public String encodeRedirectUrl(String url) {
		return response.encodeRedirectUrl(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		System.out.println("RESP sendError " + sc + " " + msg);
		response.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException {
		System.out.println("RESP sendError " + sc);
		response.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		System.out.println("RESP sendRedirect " + location);
		response.sendRedirect(location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		System.out.println("RESP setDateHeader " + name + " " + date);
		response.setDateHeader(name, date);
	}

	@Override
	public void addDateHeader(String name, long date) {
		System.out.println("RESP addDateHeader " + name);
		response.addDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		System.out.println("RESP setHeader " + name + " " + value);
		response.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		System.out.println("RESP addHeader " + name + " " + value);
		response.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		System.out.println("RESP setIntHeader " + name + " " + value);
		response.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		System.out.println("RESP addIntHeader " + name + " " + value);
		response.addIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc) {
		System.out.println("RESP setStatus:" + sc);
		response.setStatus(sc);
	}

	@Override
	public void setStatus(int sc, String sm) {
		System.out.println("RESP setStatus:" + sc + " " + sm);
		response.setStatus(sc, sm);
	}

	@Override
	public int getStatus() {
		System.out.println("RESP getStatus");
		return response.getStatus();
	}

	@Override
	public String getHeader(String name) {
		System.out.println("RESP getHeader " + name);
		return response.getHeader(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		System.out.println("RESP getHeaders " + name);
		return response.getHeaders(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		System.out.println("RESP getHeaderNames");
		return response.getHeaderNames();
	}
}
