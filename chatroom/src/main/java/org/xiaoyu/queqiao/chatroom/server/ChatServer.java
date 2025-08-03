package org.xiaoyu.queqiao.chatroom.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.chatroom.protocol.CustomProtocolFrameDecoder;
import org.xiaoyu.queqiao.chatroom.protocol.MessageCodecSharable;
import org.xiaoyu.queqiao.chatroom.server.handler.ChatRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupChatRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupCreateRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupJoinRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupMembersRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.GroupQuitRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.LoginRequestMsgHandler;
import org.xiaoyu.queqiao.chatroom.server.handler.QuitHandler;

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
        GroupJoinRequestMsgHandler groupJoinRequestMsgHandler = new GroupJoinRequestMsgHandler();
        GroupMembersRequestMsgHandler groupMembersRequestMsgHandler = new GroupMembersRequestMsgHandler();
        GroupQuitRequestMsgHandler groupQuitRequestMsgHandler = new GroupQuitRequestMsgHandler();
        GroupChatRequestMsgHandler groupChatRequestMsgHandler = new GroupChatRequestMsgHandler();
        QuitHandler quitHandler = new QuitHandler();



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
                            // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                            // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                            sc.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            // ChannelDuplexHandler 可以同时作为入站和出站处理器
                            sc.pipeline().addLast(new ChannelDuplexHandler() {
                                // userEventTriggered 用来处理特殊事件，比如用户自定义产生的
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    // 触发了读空闲事件
                                    if (event.state() == IdleState.READER_IDLE) {
                                        log.debug("已经 5s 没有读到数据了");
                                        ctx.channel().close();
                                    }
                                }
                            });
                            sc.pipeline().addLast(loginRequestMsgHandler);
                            sc.pipeline().addLast(chatRequestMsgHandler);
                            sc.pipeline().addLast(groupCreateRequestMsgHandler);
                            sc.pipeline().addLast(groupJoinRequestMsgHandler);
                            sc.pipeline().addLast(groupMembersRequestMsgHandler);
                            sc.pipeline().addLast(groupQuitRequestMsgHandler);
                            sc.pipeline().addLast(groupChatRequestMsgHandler);
                            sc.pipeline().addLast(quitHandler);
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
