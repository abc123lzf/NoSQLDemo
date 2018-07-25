package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import lzf.webserver.Host;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午9:18:39
* @Description 类说明
*/
public final class StandardEngineValve extends ValveBase {

	StandardEngineValve() {
		super();
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		Host host = request.getHost();
		//如果没有找到Host，即Mapper映射没有找到所属的Host容器，那么直接发给客户端400错误
		if(host == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "主机名:" 
					+ request.getServerName() + "不存在");
			return;
		}
		
		//传送给Host容器的管道
		host.getPipeline().getFirst().invoke(request, response);
	}
}