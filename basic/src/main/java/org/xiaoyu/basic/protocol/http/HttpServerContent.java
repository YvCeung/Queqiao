package org.xiaoyu.basic.protocol.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;


/**
 * @Description 上个例子中只能接受到请求行和请求头，这个例子演示下接收到的请求体的内容
 * @Author zy
 * @Date 2025/7/6 16:10
 **/
@Slf4j
public class HttpServerContent {
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
                        sc.pipeline().addLast(new HttpServerCodec());
                        //加入入站消息读处理器

                        // 使用SimpleChannelInboundHandler，通过泛型控制我只感兴趣的消息类型，只有是这样的，才会进入处理逻辑
                        sc.pipeline().addLast(new SimpleChannelInboundHandler<HttpContent>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpContent msg) throws Exception {

                                String content = msg.content().toString(Charset.defaultCharset());
                                log.debug("接收到的请求体的内容为 : {}", content);

                                // 返回响应
                                DefaultFullHttpResponse response =
                                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

                                byte[] bytes = "<h1>Hello, world</h1>".getBytes();
                                response.content().writeBytes(bytes);

                                // 设置长度，要不然浏览器会一直请求
                                response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                channelHandlerContext.writeAndFlush(response);
                            }
                        });

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
