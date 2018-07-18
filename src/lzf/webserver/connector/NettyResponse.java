package lzf.webserver.connector;

import io.netty.channel.ChannelHandlerContext;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月18日 上午11:04:40
* @Description 由NettyHandler构建的Netty专用Response类
*/
public class NettyResponse extends Response {

	private final ChannelHandlerContext ctx;
	
	public NettyResponse(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	
}
