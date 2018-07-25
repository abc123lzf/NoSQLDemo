package lzf.webserver.core;

import java.io.IOException;

import javax.servlet.ServletException;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.connector.Request;
import lzf.webserver.connector.RequestFacade;
import lzf.webserver.connector.Response;
import lzf.webserver.connector.ResponseFacade;
import lzf.webserver.util.ByteBufOutputStream;
import lzf.webserver.util.ByteBufPrintWriter;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午3:08:57
* @Description 标准Wrapper阀门
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
		Context context = request.getContext();
		
		context.getFilterChain().doFilter(requestFacade, responseFacade);
		
		wrapper.getServlet().service(requestFacade, responseFacade);
		
		response.setStatus(200);
		response.addDateHeader("Date", System.currentTimeMillis());
		
		if(response.getHeader("Content-Length") == null) {
		
			int charSize = 0, byteSize = 0;
			
			if(response.getWriter() instanceof ByteBufPrintWriter) {
				charSize = ((ByteBufPrintWriter)response.getWriter()).getSize();
			}
			
			if(response.getOutputStream() instanceof ByteBufOutputStream) {
				byteSize = ((ByteBufOutputStream)response.getOutputStream()).getSize();
			}
			
			response.addIntHeader("Content-Length", charSize + byteSize);
		}
		
		//context.getFilterChain().doFilter(requestFacade, responseFacade);
		
		response.sendResponse();
	}

}