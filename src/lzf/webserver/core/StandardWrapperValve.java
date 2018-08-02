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
* @author ���ӷ�
* @version 1.0
* @date 2018��7��21�� ����3:08:57
* @Description ��׼Wrapper����
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
		
		context.getListenerContainer().runRequestInitializedEvent(requestFacade);
		
		//ִ�й�����
		context.getFilterChain().doFilter(requestFacade, responseFacade);
		//ִ��Servlet
		wrapper.getServlet().service(requestFacade, responseFacade);
		
		response.setStatus(200);
		response.addDateHeader("Date", System.currentTimeMillis());
		
		setContentLength(response);
		
		//context.getFilterChain().doFilter(requestFacade, responseFacade);
		
		if(!response.isCommitted())
			context.getListenerContainer().runRequestDestroyedEvent(requestFacade);
		
		response.sendResponse();
	}
	
	/**
	 * ����Ӧͷ�����Ӧ�峤����Ϣ
	 * @param response ��Ӧ����
	 * @throws IOException
	 */
	private void setContentLength(Response response) throws IOException {
		
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
	}
}