package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.Wrapper;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.RequestFacade;
import lzf.webserver.connector.Response;
import lzf.webserver.connector.ResponseFacade;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午3:08:57
* @Description 类说明
*/
public final class StandardWrapperValve extends ValveBase {
	
	StandardWrapperValve() {
		super();
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		
		Wrapper wrapper = request.getWrapper();
		
		wrapper.getServlet().service(requestFacade, responseFacade);
		
		response.setStatus(200);
		
		log.info("Recive request in WrapperValve");
		response.sendResponse();
	}

}