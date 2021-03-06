package net.demo.netty.advanced.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HexDumpProxy {

	private final int localPort;
	private final String remoteHost;
	private final int remotePort;

	public HexDumpProxy(int localPort, String remoteHost, int remotePort) {
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	public void run() throws Exception {
		System.err.println("Proxying *:" + localPort + " to " + remoteHost + ':' + remotePort + " ...");

		// Configure the bootstrap.
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new HexDumpProxyInitializer(remoteHost, remotePort))
					.childOption(ChannelOption.AUTO_READ, false).bind(localPort).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		// Validate command line options.
		if (args.length != 3) {
			System.err.println("Usage: " + HexDumpProxy.class.getSimpleName()
					+ " <local port> <remote host> <remote port>");
			return;
		}

		// Parse command line options.
		int localPort = Integer.parseInt(args[0]);
		String remoteHost = args[1];
		int remotePort = Integer.parseInt(args[2]);

		new HexDumpProxy(localPort, remoteHost, remotePort).run();
	}
}
