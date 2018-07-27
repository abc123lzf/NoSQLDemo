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
import lzf.webserver.util.DefaultServletInputStream;
import lzf.webserver.util.StringManager;

/**
* @author ���ӷ�
* @version 1.0
* @date 2018��7��15�� ����11:10:50
* @Description 
*/
public final class NettyRequest extends Request {
	
	private static final StringManager sm = StringManager.getManager(NettyRequest.class);

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
	 * ����FullHttpRequest�����ݽ���
	 */
	private void decode() {
		
		//��ȡ�ͻ���IP���˿���Ϣ
		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
		super.remoteAddr = address.getAddress().getHostAddress();
		super.remoteHost = address.getHostName();
		super.remotePort = address.getPort();
		
		//��ȡ������
		super.method = req.getMethod().name();
		super.requestUrl = req.getUri();
		super.protocol = req.getProtocolVersion().text();
		
		//��ȡ����ͷ
		List<Map.Entry<String, String>> list = header.entries();
		
		//System.out.println(method);
		System.out.println(requestUrl);
		//System.out.println(protocol);
		
		for(Map.Entry<String, String> entry : list) {
			super.putHeader(entry.getKey(), entry.getValue());
			//System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		
		byte[] content = new byte[contentBuf.capacity()];
		
		contentBuf.readBytes(content);
		
		InputStream is = new ByteArrayInputStream(content);
		super.sis = new DefaultServletInputStream(is);
		
		InputStreamReader isr = new InputStreamReader(is);
		super.contentReader = new BufferedReader(isr);
	}
	
	/**
	 * ��ͻ���д����Ϣ(HTTP��Ӧ)
	 * @param msg ��Ϣ
	 */
	public void writeToChannel(Object msg) {
		if(msg == null)
			throw new IllegalArgumentException();
		
		ctx.write(msg);
	}
	
	/**
	 * ����һ����װ������HTTP�����NettyRequest����
	 * @param request Netty����HTTP������FullHttpRequest
	 * @param ctx ChannelHandlerContextʵ��
	 * @return ��װ������HTTP�����Request����
	 */
	static Request newRequest(FullHttpRequest request, ChannelHandlerContext ctx) {
		if(request == null || ctx == null)
			throw new IllegalArgumentException(sm.getString("NettyRequest.newRequest.e0"));
		
		NettyRequest req = new NettyRequest(request, ctx);
		req.decode();
		return req;
	}
}
