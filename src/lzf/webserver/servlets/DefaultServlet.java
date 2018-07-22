package lzf.webserver.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午8:08:34
* @Description 类说明
*/
public class DefaultServlet extends HttpServlet {

	private static final long serialVersionUID = -6022554049128780788L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setContentLength("<html><head><title>HelloWorld</title></head><p>HelloWorld</p></html>".length());
		response.getOutputStream().write("<html><head><title>HelloWorld</title></head><p>HelloWorld</p></html>".getBytes());
		
		HttpSession session = request.getSession();
		
		if(session.getAttribute("user") == null)
			session.setAttribute("user", "abc123lzf");
		else
			System.out.println("From Default Servlet:" + session.getAttribute("user"));
		
	}

}
