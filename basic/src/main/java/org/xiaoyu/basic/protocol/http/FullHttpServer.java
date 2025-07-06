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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.basic.protocol.http.verify.HttpRequestParamWrapper;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;


/**
 * @Description 这个例子演示接受完整的http请求
 * @Author zy
 * @Date 2025/7/6 16:10
 **/
@Slf4j
public class FullHttpServer {
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

                        /**
                         * 这是重点
                         * HttpObjectAggregator 的作用：
                         * 将多个 HTTP 消息片段聚合成一个完整的 FullHttpRequest 或 FullHttpResponse。
                         *
                         * 背景知识：
                         * Netty 的 HttpServerCodec 解码后，HTTP 请求体是 分段的。
                         *
                         * 举个例子：
                         *
                         * HttpRequest         // 包含请求行和头
                         * HttpContent         // Body 的一部分
                         * LastHttpContent     // Body 的最后一部分
                         * 这三部分在 Netty 中是分开发的，也就是说：
                         * 写 channelRead(ctx, Object msg) 时，可能需要收集这些碎片手动拼接。
                         *
                         * 但用了 HttpObjectAggregator 之后：
                         * pipeline.addLast(new HttpObjectAggregator(10 * 1024 * 1024)); // 聚合最大10MB
                         * 就可以这样安心写：
                         * channelRead(ChannelHandlerContext ctx, FullHttpRequest msg)
                         * 会拿到：
                         * 完整的 Header
                         * 完整的 Body
                         * 一个 FullHttpRequest 对象
                         */
                        sc.pipeline().addLast(new HttpObjectAggregator(1048576));
                        //加入入站消息读处理器

                        // 使用SimpleChannelInboundHandler，通过泛型控制我只感兴趣的消息类型，只有是这样的，才会进入处理逻辑
                        sc.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest msg) throws Exception {
                                // 这个类可以验证数据的内容
                                HttpRequestParamWrapper httpRequestParamWrapper = new HttpRequestParamWrapper(msg);
                                Map<String, List<String>> allParamsAsMultiMap = httpRequestParamWrapper.getAllParamsAsMultiMap();

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
