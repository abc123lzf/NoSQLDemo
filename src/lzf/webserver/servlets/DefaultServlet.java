package lzf.webserver.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lzf.webserver.util.ContentType;


/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��21�� ����8:08:34
* @Description ��̬�ļ����������Servlet���д���
*/
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -6022554049128780788L;
	
	private byte[] resource;
	
	private String contentType;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(contentType);
		response.addIntHeader("Content-Length", resource.length);
		response.getOutputStream().write(resource);
	}
	
	public DefaultServlet(String fileName, byte[] resource) {
		
		this.resource = resource;
		int index = fileName.lastIndexOf('.');
		String suffix = fileName.substring(index + 1, fileName.length());

		if(suffix != null) {
			this.contentType = ContentType.getContentBySuffix(suffix);
		} else {
			this.contentType = ContentType.getContentBySuffix("application/octet-stream");
		}
	}

}
