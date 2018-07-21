package lzf.webserver.connector;

import java.io.PrintWriter;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月18日 上午11:04:40
 * @Description 由NettyHandler构建的Netty专用Response类
 */
public class NettyResponse extends Response {

	private final ChannelHandlerContext ctx;

	private final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			HttpResponseStatus.OK);
	
	private ByteBuf content = response.content();

	public NettyResponse(ChannelHandlerContext ctx) {
		super();
		super.sos = new lzf.webserver.util.ByteBufOutputStream(content);
		super.pw = new PrintWriter(new io.netty.buffer.ByteBufOutputStream(content));
		this.ctx = ctx;
	}

	public static Response newResponse(ChannelHandlerContext ctx) {
		return new NettyResponse(ctx);
	}

	@Override
	public synchronized void sendResponse() {
		response.setStatus(HttpResponseStatus.valueOf(200)); //status
		
		for(Map.Entry<String, String> entry : headerMap.entrySet())
			response.headers().add(entry.getKey(), entry.getValue());
		System.out.println("send!!!");
		ctx.writeAndFlush(response);
		super.committed = true;
	}

	@Override
	public synchronized void reset() {
		super.reset();
		response.content().clear();
	}

	@Override
	protected void sendError0(int sc, String msg) {
		response.setStatus(HttpResponseStatus.valueOf(sc));
		ctx.writeAndFlush(response);
	}

	@Override
	protected void sendError0(int sc) {
		response.setStatus(HttpResponseStatus.valueOf(sc));
		ctx.writeAndFlush(response);
	}
	
	@Override
	public void setBufferSize(int size) {
		synchronized(content) {
			content.capacity(size);
		}
	}

	@Override
	public int getBufferSize() {
		return content.capacity();
	}
	
	@Override
	public void resetBuffer() {
		synchronized(content) {
			content.clear();
		}
	}
}
