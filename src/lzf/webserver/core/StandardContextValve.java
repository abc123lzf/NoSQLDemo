package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 上午10:38:27
* @Description Context基础阀门
*/
public class StandardContextValve extends ValveBase {
	
	StandardContextValve() {
		super();
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		Wrapper wrapper = request.getWrapper();
		if(wrapper == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
