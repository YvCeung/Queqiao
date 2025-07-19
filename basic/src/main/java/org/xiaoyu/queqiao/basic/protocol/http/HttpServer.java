package org.xiaoyu.queqiao.basic.protocol.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;


/**
 * @Description 演示接受的请求行和请求头
 * @Author zy
 * @Date 2025/7/6 16:10
 **/
@Slf4j
public class HttpServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        // HttpServerCodec 专门用于解析http协议的一个codec
                        sc.pipeline().addLast(new HttpServerCodec());
                        //加入入站消息读处理器

                        // 使用SimpleChannelInboundHandler，通过泛型控制我只感兴趣的消息类型，只有是这样的，才会进入处理逻辑
                        sc.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) throws Exception {
                                // 获取请求
                                log.debug(httpRequest.uri());

                                // 返回响应
                                DefaultFullHttpResponse response =
                                        new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);

                                byte[] bytes = "<h1>Hello, world</h1>".getBytes();
                                response.content().writeBytes(bytes);

                                // 设置长度，要不然浏览器会一直请求
                                response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                channelHandlerContext.writeAndFlush(response);
                            }
                        });

                        /**
                         * 传统的处理方式
                        sc.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 通过日志可以看到，虽然只发送了一次请求，但是到了netty这边却解析成了两个部分，所以得需要针对不同类型
                                // 做不同的处理
                                log.debug("{}", msg.getClass());

                                // 即： 请求行和请求头
                                if(msg instanceof HttpRequest){

                                // 请求体
                                }else if(msg instanceof HttpContent){

                                }
                            }
                        });*/
                    }
                });

        ChannelFuture channelFuture = null;
        try {
            channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error when connection", e, e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
