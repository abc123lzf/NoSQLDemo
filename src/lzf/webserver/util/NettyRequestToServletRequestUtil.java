package lzf.webserver.util;

import io.netty.handler.codec.http.FullHttpRequest;
import lzf.webserver.connector.Request;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月14日 下午10:01:15
* @Description Netty的FullHttpReuest转化为Servlet规范的Request
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
