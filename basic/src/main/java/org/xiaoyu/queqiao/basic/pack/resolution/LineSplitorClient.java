package org.xiaoyu.queqiao.basic.pack.resolution;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 固定分隔符的方式解决半包黏包-客户端
 */
@Slf4j
public class LineSplitorClient {


        public static void main(String[] args) {
            NioEventLoopGroup worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.group(worker);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("connetted...");
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.debug("sending...");
                                Random r = new Random();
                                char c = 'a';
                                ByteBuf buffer = ctx.alloc().buffer();
                                for (int i = 0; i < 10; i++) {
                                    for (int j = 1; j <= r.nextInt(16)+1; j++) {
                                        buffer.writeByte((byte) c);
                                    }
                                    // 追加换行符
                                    buffer.writeByte(10);
                                    c++;
                                }
                                ctx.writeAndFlush(buffer);
                            }
                        });
                    }
                });
                ChannelFuture channelFuture = bootstrap.connect("localhost", 9090).sync();
                channelFuture.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                log.error("client error", e);
            } finally {
                worker.shutdownGracefully();
            }
        }
}
