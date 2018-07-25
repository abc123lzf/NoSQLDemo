package lzf.webserver.connector;

import java.io.IOException;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lzf.webserver.util.ByteBufOutputStream;
import lzf.webserver.util.ByteBufPrintWriter;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月18日 上午11:04:40
 * @Description 由NettyHandler构建的Netty专用Response类
 */
public final class NettyResponse extends Response {

	private final ChannelHandlerContext ctx;

	private final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			HttpResponseStatus.OK);
	
	private ByteBuf content = response.content();

	private NettyResponse(ChannelHandlerContext ctx) {
		super();
		ByteBufOutputStream bbos = new ByteBufOutputStream(content);
		super.sos = bbos;
		super.pw = new ByteBufPrintWriter(bbos, content);
		this.ctx = ctx;
	}

	static Response newResponse(ChannelHandlerContext ctx) {
		return new NettyResponse(ctx);
	}

	@Override
	public synchronized void sendResponse() {
		response.setStatus(HttpResponseStatus.valueOf(status)); //应从Request中获取status
		
		for(Map.Entry<String, String> entry : headerMap.entrySet())
			response.headers().add(entry.getKey(), entry.getValue());
		
		ctx.writeAndFlush(response);
		super.committed = true;
	}
	
	
	/**
	 * 重定向页面
	 * @param location 跳转URL，可以为绝对路径也可为相对路径
	 */
	@Override
	public final void sendRedirect(String location) throws IOException {
		setStatus(302);
		headerMap.put("Location", location);
		sendResponse();
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
