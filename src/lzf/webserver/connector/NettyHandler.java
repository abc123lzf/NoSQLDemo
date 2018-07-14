package lzf.webserver.connector;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author 李子帆
 * @version 1.0
 * @date 2018年7月14日 下午7:43:49
 * @Description Netty NIO接收器
 */
public class NettyHandler implements Handler {

	private int port;
	private Connector connector;

	private volatile HandlerState state = HandlerState.NEW;

	private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final Processer processer = new Processer();

	private final EventLoopGroup acceptGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	private ServerSocketChannel serverChannel = null;
	
	public NettyHandler() {
	}

	public NettyHandler(int port, Connector connector) {
		this();
		this.connector = connector;
		this.port = port;
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
		
		state = HandlerState.INITIALIZING;
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(acceptGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HttpResponseEncoder());
						ch.pipeline().addLast(new HttpRequestDecoder());
					}
				}).option(ChannelOption.SO_BACKLOG, connector.getMaxConnection())
				.option(ChannelOption.SO_TIMEOUT, connector.getTimeOut());
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
		if(state == HandlerState.ERR || state.after(HandlerState.INITIALIZED))
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
}
