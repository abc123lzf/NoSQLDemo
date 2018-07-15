package lzf.webserver.connector;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 上午11:10:50
* @Description 
*/
public class NettyRequest extends Request {

	private final FullHttpRequest req;
	
	private final HttpHeaders header;
	
	public NettyRequest(FullHttpRequest request) {
		this.req = request;
		this.header = req.headers();
		decode();
	}
	
	private void decode() {
		super.method = req.getMethod().name();
		super.uri = req.getUri();
		super.protocol = req.getProtocolVersion().text();
		
		header.get(HttpHeaders.Names.HOST);
		header.get(HttpHeaders.Names.ACCEPT);
		header.get(HttpHeaders.Names.DATE);
		super.contentLength = Integer.valueOf(header.get(HttpHeaders.Names.CONTENT_LENGTH));
		header.get(HttpHeaders.Names.COOKIE);
		super.contentType = header.get(HttpHeaders.Names.CONTENT_TYPE);
		super.serverName = header.get(HttpHeaders.Names.SERVER);
	}
}
