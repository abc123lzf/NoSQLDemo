package lzf.webserver.connector;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月14日 下午7:43:49
 * @Description Netty NIO接收器
 */
public class NettyHandler implements Handler {

	private int port = Connector.DEFAULT_PORT;
	private Connector connector;

	private volatile HandlerState state = HandlerState.NEW;

	private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final Processer processer = new Processer();

	private final EventLoopGroup acceptGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	private ServerSocketChannel serverChannel = null;
	
	public NettyHandler() {
	}

	public NettyHandler(Connector connector) {
		this();
		this.connector = connector;
		this.port = connector.getPort();
	}

	private class Processer implements Runnable {
		@Override
		public void run() {
			try {
				serverChannel.closeFuture().sync();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	@Override
	public Connector getConnector() {
		return connector;
	}

	@Override
	public void init() throws HandlerException {
		if(state == HandlerState.ERR || state.after(HandlerState.INITIALIZING))
			throw new HandlerException("无法初始化该Handler");
		
		//TODO 检查当前变量是否设置完全，否则抛出异常
		
		state = HandlerState.INITIALIZING;
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(acceptGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HttpResponseEncoder());
						ch.pipeline().addLast(new HttpRequestDecoder());
						ch.pipeline().addLast(new HttpServerCodec());
						ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
						ch.pipeline().addLast(new HttpServerInboundHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, connector.getMaxConnection())
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connector.getTimeOut());
		try {
			ChannelFuture future = boot.bind(port).sync();
			if(future.isSuccess()) {
				serverChannel = (ServerSocketChannel)future.channel();
			} else {
				state = HandlerState.ERR;
				throw new HandlerException("绑定端口失败");
			}
		} catch (InterruptedException e) {
			state = HandlerState.ERR;
			throw new HandlerException("绑定端口被打断");
		}
		state = HandlerState.INITIALIZED;
	}

	@Override
	public void start() throws HandlerException {
		if(state == HandlerState.ERR || state.after(HandlerState.STARTING))
			throw new HandlerException("无法启动该Handler");
		
		state = HandlerState.STARTING;
		executor.execute(processer);
		state = HandlerState.STARTED;
	}

	@Override
	public void stop() throws HandlerException {
		if(state == HandlerState.ERR || state.after(HandlerState.INITIALIZING))
			throw new HandlerException("无法停止该Handler");
		
		state = HandlerState.STOPPING;
		acceptGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		state = HandlerState.STOPPED;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	@Override
	public HandlerState getState() {
		return state;
	}
	
	class RequestProcesser implements Runnable {

		private FullHttpRequest fullRequest;
		
		public RequestProcesser(FullHttpRequest request) {
			this.fullRequest = request;
		}
		
		@Override
		public void run() {
			//TODO 转化为Servlet规范请求类
		}
		
	}
	
	class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
		
		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object msg) {
			if(msg instanceof FullHttpRequest) {
				FullHttpRequest request = (FullHttpRequest) msg;
				Request req = NettyRequest.newRequest(request, ctx);
				// TODO 接收到HTTP请求后将其转换为Servlet规范(用单独线程实现)
			} else { 
				ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			}
		}
		
	}
}
