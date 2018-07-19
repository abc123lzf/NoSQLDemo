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
 * @author ���ӷ�
 * @version 1.0
 * @date 2018��7��14�� ����7:43:49
 * @Description Netty NIO������
 */
public class NettyHandler extends LifecycleBase implements Handler {

	private int port = Connector.DEFAULT_PORT;
	//�ý�����������������
	private Connector connector;
	//ҵ���߼��̳߳�
	private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	//Netty���Ӵ����߳�
	private final NettyHandlerProcesser processer = new NettyHandlerProcesser();
	//���ӽ����߳���
	private final EventLoopGroup acceptGroup = new NioEventLoopGroup();
	//��վ��վ�����߳���
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	//������Socketͨ��
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
						//���ͻ��˳���10��û�з����κ������������һ����վ������TimeOutHandler��userEventTriggered����
						ch.pipeline().addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
						//�ý�վ�������������IdleStateHandler���͵��¼�������رճ�ʱTCP����
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
				throw new HandlerException("�󶨶˿�ʧ��");
			}
		} catch (InterruptedException e) {
			setLifecycleState(LifecycleState.FAILED);
			throw new HandlerException("�󶨶˿ڱ����");
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
	 * ������ҵ���߼���ר���̣߳�����ͨ������runRequestProcesserʵ��
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
	 * ����ҵ���߼��߳�
	 * @param request FullHttpRequest����
	 * @param ctx ChannelHandlerContextʵ��
	 */
	protected void runRequestProcesser(FullHttpRequest request, ChannelHandlerContext ctx) {
		executor.execute(new RequestProcesser(request, ctx));
	}
	
	/**
	 * ����FullHttpRequest��վ������������ת�������Request������ӵ�ҵ���߼��̳߳��в�ִ��
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
 * @author ���ӷ�
 * @version 1.0
 * @Description ���ͻ��˳���10���û�з���������ر����TCP����
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
