package lzf.webserver.connector;

import io.netty.channel.ChannelHandlerContext;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��18�� ����11:04:40
* @Description ��NettyHandler������Nettyר��Response��
*/
public class NettyResponse extends Response {

	private final ChannelHandlerContext ctx;
	
	public NettyResponse(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	
}
