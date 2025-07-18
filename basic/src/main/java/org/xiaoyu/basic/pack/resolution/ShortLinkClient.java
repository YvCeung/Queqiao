package org.xiaoyu.basic.pack.resolution;

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

/**
 * 采用短链接的方式进行解决
 * 发一个包建立一次连接，这样连接建立到连接断开之间就是消息的边界，缺点效率太低(只要连接断开了，缓冲区是一定会把数据交给服务器的程序处理的)
 * 对应服务端的启动类为：org.xiaoyu.basic.pack.stick.StickServer
 */
@Slf4j
public class ShortLinkClient {

        public static void main(String[] args) {
            // 分 10 次发送
            for (int i = 0; i < 10; i++) {
                send();
            }
        }

        private static void send() {
            NioEventLoopGroup worker = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.group(worker);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("connected...");
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.debug("sending...");
                                ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                ctx.writeAndFlush(buffer);
                                // 发完即关
                                ctx.close();
                            }
                        });
                    }
                });
                ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
                log.info("发送通道关闭");
                channelFuture.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                log.error("client error", e);
            } finally {
                worker.shutdownGracefully();
            }
        }

}
