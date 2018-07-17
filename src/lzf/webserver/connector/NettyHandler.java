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
import lzf.webserver.LifecycleException;
import lzf.webserver.LifecycleState;
import lzf.webserver.core.LifecycleBase;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月14日 下午7:43:49
 * @Description Netty NIO接收器
 */
public class NettyHandler extends LifecycleBase implements Handler {

	private int port = Connector.DEFAULT_PORT;
	//该接收器所属的连接器
	private Connector connector;
	//线程池
	private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private final NettyHandlerProcesser processer = new NettyHandlerProcesser();

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

	protected class NettyHandlerProcesser implements Runnable {
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
	public Executor getExecutor() {
		return executor;
	}
	
	@Override
	protected void initInternal() throws LifecycleException {
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
				setLifecycleState(LifecycleState.FAILED);
				throw new LifecycleException("绑定端口失败");
			}
		} catch (InterruptedException e) {
			setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("绑定端口被打断");
		}
	}
	
	@Override
	protected void startInternal() throws LifecycleException {
		executor.execute(processer);
	}

	@Override
	protected void stopInternal() throws LifecycleException {
		acceptGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	@Override
	protected void destoryInternal() throws LifecycleException {
		
	}

	
	protected static class RequestProcesser implements Runnable {
		private final FullHttpRequest fullRequest;
		private final ChannelHandlerContext ctx;
		
		public RequestProcesser(FullHttpRequest request, ChannelHandlerContext ctx) {
			this.fullRequest = request;
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			Request request = NettyRequest.newRequest(fullRequest, ctx);
			//TODO 提交到Engine管道
		}
	}
	
	protected void runRequestProcesser(FullHttpRequest request, ChannelHandlerContext ctx) {
		executor.execute(new RequestProcesser(request, ctx));
	}
	
	class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
		
		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object msg) {
			if(msg instanceof FullHttpRequest) {
				FullHttpRequest request = (FullHttpRequest) msg;
				runRequestProcesser(request, ctx);
			} else { 
				ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			}
		}
		
	}
}
