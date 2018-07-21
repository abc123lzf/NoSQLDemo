package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

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
		
		context.getPipeline().getFirst().invoke(request, response);
	}
}
