package lzf.webserver.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月15日 上午11:10:50
* @Description 
*/
public class NettyRequest extends Request {

	private final ChannelHandlerContext ctx;
	
	private final FullHttpRequest req;
	
	private final HttpHeaders header;
	
	private final ByteBuf contentBuf;
	
	private NettyRequest(FullHttpRequest request, ChannelHandlerContext ctx) {
		this.req = request;
		this.header = req.headers();
		this.ctx = ctx;
		this.contentBuf = req.content();
	}
	
	/**
	 * 根据FullHttpRequest的内容解码
	 */
	private void decode() {
		//获取客户端IP、端口信息
		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
		super.remoteAddr = address.getAddress().getHostAddress();
		super.remoteHost = address.getHostName();
		super.remotePort = address.getPort();
		//获取请求行
		super.method = req.getMethod().name();
		super.requestUrl = req.getUri();
		super.protocol = req.getProtocolVersion().text();
		//获取请求头
		List<Map.Entry<String, String>> list = header.entries();
		
		System.out.println(method);
		System.out.println(requestUrl);
		System.out.println(protocol);
		for(Map.Entry<String, String> entry : list) {
			super.putHeader(entry.getKey(), entry.getValue());
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		byte[] content = contentBuf.array();
		InputStream is = new ByteArrayInputStream(content);
		InputStreamReader isr = new InputStreamReader(is);
		super.contentReader = new BufferedReader(isr);
	}
	
	/**
	 * 向客户端写入消息(HTTP响应)
	 * @param msg 消息
	 */
	public void writeToChannel(Object msg) {
		if(msg == null)
			throw new IllegalArgumentException();
		
		ctx.write(msg);
	}
	
	/**
	 * 返回一个封装了完整HTTP请求的NettyRequest对象
	 * @param request Netty完整HTTP请求类FullHttpRequest
	 * @param ctx ChannelHandlerContext实例
	 * @return 封装了完整HTTP请求的Request对象
	 */
	public static Request newRequest(FullHttpRequest request, ChannelHandlerContext ctx) {
		if(request == null || ctx == null)
			throw new IllegalArgumentException();
		
		NettyRequest req = new NettyRequest(request, ctx);
		req.decode();
		return req;
	}
}
