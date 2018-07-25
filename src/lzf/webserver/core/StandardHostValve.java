package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lzf.webserver.Context;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 上午10:30:39
* @Description Host容器基础阀门
*/
public final class StandardHostValve extends ValveBase {
	
	StandardHostValve() {
		super();
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		Context context = request.getContext();
		
		if(context == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		String uri = request.getRequestURI();
		
		//拦截尝试访问/WEB-INF文件夹的请求
		if(context.getName().equals("ROOT")) {
			if(uri.toLowerCase().startsWith("/web-inf")) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		} else {
			if(uri.toLowerCase().startsWith("/" + context.getName().toLowerCase() + "/web-inf")) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		
		HttpSession serverSession = request.getSession(false);
		
		if(serverSession == null) {
			
			serverSession = request.getSession();
			Cookie session = context.createSessionCookie(serverSession.getId());
			
			response.addCookie(session);
		}

		context.getPipeline().getFirst().invoke(request, response);
	}
}
