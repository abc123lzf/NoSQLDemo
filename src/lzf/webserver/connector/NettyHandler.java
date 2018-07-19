package lzf.webserver.connector;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
	//业务逻辑线程池
	private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	//Netty连接处理线程
	private final NettyHandlerProcesser processer = new NettyHandlerProcesser();
	//连接接收线程组
	private final EventLoopGroup acceptGroup = new NioEventLoopGroup();
	//进站出站处理线程组
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	//服务器Socket通道
	private volatile ServerSocketChannel serverChannel = null;
	
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
	protected void initInternal() throws LifecycleException, HandlerException {
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(acceptGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						//若客户端超过10秒没有发送任何请求则调用下一个进站处理器TimeOutHandler的userEventTriggered方法
						ch.pipeline().addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
						//该进站处理器负责接收IdleStateHandler发送的事件并负责关闭超时TCP连接
						ch.pipeline().addLast(new TimeOutHandler());
						ch.pipeline().addLast(new HttpResponseEncoder());
						ch.pipeline().addLast(new HttpRequestDecoder());
						//ch.pipeline().addLast(new HttpServerCodec());
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
				throw new HandlerException("绑定端口失败");
			}
		} catch (InterruptedException e) {
			setLifecycleState(LifecycleState.FAILED);
			throw new HandlerException("绑定端口被打断");
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
		//NOOP
	}

	/**
	 * 负责处理业务逻辑的专用线程，必须通过调用runRequestProcesser实现
	 */
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
			Response response = NettyResponse.newResponse(ctx);
			//ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	/**
	 * 运行业务逻辑线程
	 * @param request FullHttpRequest对象
	 * @param ctx ChannelHandlerContext实例
	 */
	protected void runRequestProcesser(FullHttpRequest request, ChannelHandlerContext ctx) {
		executor.execute(new RequestProcesser(request, ctx));
	}
	
	/**
	 * 接收FullHttpRequest进站处理器，负责转化将这个Request对象添加到业务逻辑线程池中并执行
	 */
	class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
		
		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object msg) {
			if(msg instanceof FullHttpRequest) {
				FullHttpRequest request = (FullHttpRequest) msg;
				runRequestProcesser(request, ctx);
			}
		}
	}
}

/**
 * @author 李子帆
 * @version 1.0
 * @Description 若客户端超过10秒后没有发送请求则关闭这个TCP连接
 */
class TimeOutHandler extends ChannelInboundHandlerAdapter {
	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			if(event.state() == IdleState.READER_IDLE) {
				System.out.println("Channel Close");
				ctx.channel().close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
