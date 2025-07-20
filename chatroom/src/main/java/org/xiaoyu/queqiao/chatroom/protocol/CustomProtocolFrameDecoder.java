package org.xiaoyu.queqiao.chatroom.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Description 因为LengthFieldBasedFrameDecoder用的是固定的数值构造，不如直接封装一下，不让参数暴露在上层
 * @Author zy
 * @Date 2025/7/19 15:58
 **/
public class CustomProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {
    public CustomProtocolFrameDecoder(){
        this(1024, 12, 4, 0, 0);

    }
    public CustomProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
