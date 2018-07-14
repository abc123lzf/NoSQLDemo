package lzf.webserver;

import javax.servlet.Servlet;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����9:39:00
* @Description ��ײ����������浥��JSP��Servletʵ��
*/
public interface Wrapper extends Container {

	public void addServlet(Servlet servlet);
	
	public Servlet getServlet();
	
	public void invoke(Request request, Response response);
}
