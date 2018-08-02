package lzf.webserver.core;

import java.io.IOException;
import java.util.List;

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
import lzf.webserver.mapper.ContextMapper;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 下午4:45:05
* @Description 类说明
*/
public class ApplicationRequestDispatcher implements RequestDispatcher {
	
	private final Context context;
	
	private String uri;
	
	private Request request;
	
	private Wrapper wrapper;
	
	public ApplicationRequestDispatcher(Context context, String uri, Request request) {
		this.context = context;
		this.uri = uri;
		this.request = request;
		this.wrapper = null;
	}
	
	public ApplicationRequestDispatcher(Context context, String uri) {
		this.context = context;
		this.uri = uri;
		this.request = null;
		this.wrapper = null;
	}
	
	/**
	 * @param context 所属的Context容器
	 * @param name Wrapper容器名(Servlet名称)
	 */
	public ApplicationRequestDispatcher(Context context, Wrapper wrapper) {
		this.context = context;
		this.uri = null;
		this.request = null;
		this.wrapper = wrapper;
	}
	
	private void prepareForward(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(RequestDispatcher.FORWARD_REQUEST_URI, req.getRequestURI());
		req.setAttribute(RequestDispatcher.FORWARD_CONTEXT_PATH, req.getContextPath());
		req.setAttribute(RequestDispatcher.FORWARD_SERVLET_PATH, req.getServletPath());
		req.setAttribute(RequestDispatcher.FORWARD_PATH_INFO, req.getPathInfo());
		req.setAttribute(RequestDispatcher.FORWARD_QUERY_STRING, req.getQueryString());
	}

	@Override
	public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		
		
		prepareForward((HttpServletRequest)req, (HttpServletResponse)res);
		
		if(request == null && wrapper == null) {
			
			req.getRequestDispatcher(uri).forward(req, res);
			return;
			
		} else if(request == null && wrapper != null) {
			
			List<String> uriPatterns = ((StandardWrapper)wrapper).getURIPatterns();
			
			for(String uriPattern : uriPatterns) {
				if(uriPattern.indexOf('*') == -1) {
					req.getRequestDispatcher(uriPattern).forward(req, res);
					return;
				}
			}
			
			return;
		}

		HttpServletResponse response = (HttpServletResponse) res;

		if(response.isCommitted())
			throw new IllegalStateException("Response has been commit");

		response.resetBuffer();
		
		ContextMapper mapper = context.getMapper();
		
		String forwardUri = null;
		
		//如果URI不是以/为开头的说明是相对路径
		if(!uri.startsWith("/")) {
			if(request.getRequestURI().startsWith("/"))
				forwardUri = request.getRequestURI() + uri;
			else
				forwardUri = request.getRequestURI() + "/" + uri;
		} else {
			forwardUri = uri;
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
		request.setWrapper(wrapper);
		request.cleanParameterMap();
		
		context.getPipeline().getFirst().invoke(request, request.getResponse());
	}
	
	private void prepareInclude(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(RequestDispatcher.INCLUDE_REQUEST_URI, req.getRequestURI());
		req.setAttribute(RequestDispatcher.INCLUDE_CONTEXT_PATH, req.getContextPath());
		req.setAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH, req.getServletPath());
		req.setAttribute(RequestDispatcher.INCLUDE_PATH_INFO, req.getPathInfo());
		req.setAttribute(RequestDispatcher.INCLUDE_QUERY_STRING, req.getQueryString());
	}

	@Override
	public void include(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		
		prepareInclude((HttpServletRequest)req, (HttpServletResponse)res);
		
	}

}
