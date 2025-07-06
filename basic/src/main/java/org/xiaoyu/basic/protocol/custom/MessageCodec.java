package org.xiaoyu.basic.protocol.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.xiaoyu.basic.protocol.message.Message;

import java.util.List;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/6 17:42
 **/
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * @param channelHandlerContext
     * @param message
     * @param byteBuf 是由netty已经创建好的，我们只需要按照自定义协议的格式，将message转换成byteBuf就可以
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

    }
}
