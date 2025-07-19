package org.xiaoyu.basic.pack.resolution;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/19 12:20
 **/
public class LengthFieldCase {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = getEmbeddedChannel();
        EmbeddedChannel embeddedChannel2 = getEmbeddedChannelSplit();

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        fillBuf(buffer, "Hello World");

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer();
        fillBuf(buffer2, "Hello World");

        embeddedChannel.writeInbound(buffer);
        embeddedChannel2.writeInbound(buffer2);
    }

    private static EmbeddedChannel getEmbeddedChannel() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                // 构造参数对应的含义：最大长度、长度偏移量、长度所占字节、以长度字段为基准还有几个字节才是内容、从头剥离几个字节
                // 第四个参数通常用来表示协议中的魔术值
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG)
        );
        return embeddedChannel;
    }


    private static EmbeddedChannel getEmbeddedChannelSplit() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );
        return embeddedChannel;
    }

    private static void fillBuf(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        buffer.writeInt(length);
        buffer.writeBytes(bytes);
    }
}
