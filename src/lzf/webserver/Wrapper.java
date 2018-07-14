package lzf.webserver;

import javax.servlet.Servlet;

import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 上午9:39:00
* @Description 最底层容器，保存单个JSP、Servlet实例
*/
public interface Wrapper extends Container {

	public void addServlet(Servlet servlet);
	
	public Servlet getServlet();
	
	public void invoke(Request request, Response response);
}
