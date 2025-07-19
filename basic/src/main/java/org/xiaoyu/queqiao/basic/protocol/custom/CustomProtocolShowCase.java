package org.xiaoyu.queqiao.basic.protocol.custom;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.xiaoyu.queqiao.common.message.LoginRequestMessage;

/**
 * @Description 自定义协议的演示case
 * @Author zy
 * @Date 2025/7/19 14:53
 **/
public class CustomProtocolShowCase {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                // 半包黏包问题解决 12是因为，在自定义的协议中，用来表示长度的四个字段在第13为才开始
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),
                // 自定义的解析逻辑
                new MessageCodec()
        );

        // 出站，验证encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("xiaoyu", "123456");
        embeddedChannel.writeOneOutbound(loginRequestMessage);

        // 分配一个Buf,然后手动调用encode,将msg写入到buf中
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, loginRequestMessage, buf);
        embeddedChannel.writeInbound(buf);

    }
}
