package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.Response;
import lzf.webserver.mapper.ContextMapper;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午4:45:05
* @Description 类说明
*/
public class ApplicationRequestDispatcher implements RequestDispatcher {
	
	private final Context context;
	
	private final String uri;
	
	public ApplicationRequestDispatcher(Context context, String uri) {
		this.context = context;
		this.uri = uri;
	}

	@Override
	public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		
		Request request = (Request)req;
		Response response = (Response)res;
		
		if(response.isCommitted())
			throw new IllegalStateException("Response has been commit");

		response.resetBuffer();
		
		ContextMapper mapper = context.getMapper();
		
		String forwardUri = null;
		
		if(!uri.startsWith("/")) {
			if(request.getRequestURI().startsWith("/"))
				forwardUri = request.getRequestURI() + uri;
			else
				forwardUri = request.getRequestURI() + "/" + uri;
		}
		
		Wrapper wrapper = null;
		
		if(context.getName().equals("ROOT")) {
			wrapper = mapper.getWrapper(forwardUri);
		} else {
			wrapper = mapper.getWrapper("/" + context.getName() + forwardUri);
		}
		
		if(wrapper == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			request.setDispatcherType(DispatcherType.ERROR);
			return;
		}
		
		request.setDispatcherType(DispatcherType.FORWARD);
		request.setRequestURI(forwardUri);
		request.cleanParameterMap();
		
		context.getPipeline().getFirst().invoke(request, response);
	}

	@Override
	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
