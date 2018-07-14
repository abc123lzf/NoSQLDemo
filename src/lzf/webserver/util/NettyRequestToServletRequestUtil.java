package lzf.webserver.util;

import io.netty.handler.codec.http.FullHttpRequest;
import lzf.webserver.connector.Request;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��14�� ����10:01:15
* @Description Netty��FullHttpReuestת��ΪServlet�淶��Request
*/
public final class NettyRequestToServletRequestUtil {

	public static Request decode(Request request, FullHttpRequest fullRequest) {
		
		
		return request;
	}
	
	public static Request decode(FullHttpRequest fullRequest) {
		Request request = new Request();
		return decode(request, fullRequest);
	}
	
	private NettyRequestToServletRequestUtil() {
	}
	
}
