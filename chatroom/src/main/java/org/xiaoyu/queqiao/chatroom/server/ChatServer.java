package org.xiaoyu.queqiao.chatroom.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.chatroom.protocol.CustomProtocolFrameDecoder;
import org.xiaoyu.queqiao.chatroom.protocol.MessageCodecSharable;
import org.xiaoyu.queqiao.chatroom.server.handler.ChatRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupCreateRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.LoginRequestMsgHandler;

/**
 * @Description 聊天室服务端
 * @Author zy
 * @Date 2025/7/28 23:01
 **/
@Slf4j
public class ChatServer {
    public static void main(String[] args) throws InterruptedException {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoginRequestMsgHandler loginRequestMsgHandler = new LoginRequestMsgHandler();
        ChatRequestMsgHandler chatRequestMsgHandler = new ChatRequestMsgHandler();
        GroupCreateRequestMsgHandler groupCreateRequestMsgHandler = new GroupCreateRequestMsgHandler();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                           sc.pipeline().addLast(new CustomProtocolFrameDecoder());
                           sc.pipeline().addLast(loggingHandler);
                           sc.pipeline().addLast(messageCodecSharable);
                           sc.pipeline().addLast(loginRequestMsgHandler);
                           sc.pipeline().addLast(chatRequestMsgHandler);
                           sc.pipeline().addLast(groupCreateRequestMsgHandler);
                        }
                    });
            // Netty 是异步的，bind() 会立刻返回一个 ChannelFuture，而 sync() 的作用是阻塞当前线程，直到端口绑定成功为止。
            Channel channel = serverBootstrap.bind(8080).sync().channel();

            // 返回一个 ChannelFuture，表示服务端 Channel 关闭时的未来事件。让主线程阻塞，等待 Netty 服务器关闭
            // 一旦某种逻辑调用了 channel.close()（比如优雅关闭服务），这行才会执行结束，整个程序退出。
            // 当注释掉这行代码之后程序启动后很快就结束了
            /**
             * channel.closeFuture()  返回的是一个 ChannelFuture，这个 future 的作用是监听这个 Channel 什么时候关闭。
             */
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
