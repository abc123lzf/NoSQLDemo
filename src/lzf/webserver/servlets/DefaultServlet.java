package lzf.webserver.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lzf.webserver.util.ContentType;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��21�� ����8:08:34
* @Description ��̬�ļ����������Servlet���д���
*/
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -6022554049128780788L;
	
	@SuppressWarnings("unused")
	private final File path;
	
	// �þ�̬��Դ�Ķ���������
	private final byte[] resource;
	
	// �þ�̬��Դ����
	private final String contentType;
	
	// �þ�̬��Դ����޸�ʱ��
	private final long lastModified;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType(contentType);
		response.addIntHeader("Content-Length", resource.length);
		response.setDateHeader("Last-Modified", lastModified);
		
		response.getOutputStream().write(resource);
	}
	
	
	public DefaultServlet(File path, byte[] resource) {
		
		this.path = path;
		this.resource = resource;
		
		String fileName = path.getPath();
		int index = fileName.lastIndexOf('.');
		String suffix = fileName.substring(index + 1, fileName.length());

		if(suffix != null) {
			this.contentType = ContentType.getContentBySuffix(suffix);
		} else {
			this.contentType = ContentType.getContentBySuffix("application/octet-stream");
		}
		
		this.lastModified = path.lastModified();
	}

}
