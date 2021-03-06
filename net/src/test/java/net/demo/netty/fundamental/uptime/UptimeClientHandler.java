package net.demo.netty.fundamental.uptime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 */
@Sharable
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {

	private final UptimeClient client;
	private long startTime = -1;

	public UptimeClientHandler(UptimeClient client) {
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (startTime < 0) {
			startTime = System.currentTimeMillis();
		}
		println("Connected to: " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// Discard received data
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}

		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.READER_IDLE) {
			// The connection was OK but there was no traffic for last period.
			println("Disconnecting due to no inbound traffic");
			ctx.close();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		println("Disconnected from: " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
		println("Sleeping for: " + UptimeClient.RECONNECT_DELAY + 's');

		final EventLoop loop = ctx.channel().eventLoop();
		loop.schedule(new Runnable() {
			@Override
			public void run() {
				println("Reconnecting to: " + ctx.channel().remoteAddress());
				client.configureBootstrap(new Bootstrap(), loop).connect();
			}
		}, UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof ConnectException) {
			startTime = -1;
			println("Failed to connect: " + cause.getMessage());
		}
		cause.printStackTrace();
		ctx.close();
	}

	void println(String msg) {
		if (startTime < 0) {
			System.err.format("[SERVER IS DOWN] %s%n", msg);
		} else {
			System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
		}
	}
}
