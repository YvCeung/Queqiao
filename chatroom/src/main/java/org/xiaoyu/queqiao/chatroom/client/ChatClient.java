package org.xiaoyu.queqiao.chatroom.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.chatroom.protocol.CustomProtocolFrameDecoder;
import org.xiaoyu.queqiao.chatroom.protocol.MessageCodecSharable;

/**
 * @Description 聊天室客户端
 * @Author zy
 * @Date 2025/7/19 15:54
 **/
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new CustomProtocolFrameDecoder());
                            socketChannel.pipeline().addLast(loggingHandler);
                            socketChannel.pipeline().addLast(messageCodecSharable);
                        }
                    });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }

    }
}
