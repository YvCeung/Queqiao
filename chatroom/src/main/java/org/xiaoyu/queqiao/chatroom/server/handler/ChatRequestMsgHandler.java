package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.SessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;
import org.xiaoyu.queqiao.common.message.ChatRequestMessage;
import org.xiaoyu.queqiao.common.message.ChatResponseMessage;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/29 22:01
 **/
@ChannelHandler.Sharable
public class ChatRequestMsgHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage chatRequestMessage) throws Exception {
        String to = chatRequestMessage.getTo();

        ISessionManangeService sessionManageMemoryImpl = SessionManageServiceFactory.getSessionManageService("SessionManageMemoryImpl");

        // 获取接受人的channel
        Channel channel = sessionManageMemoryImpl.getChannel(to);
        // 在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(chatRequestMessage.getFrom(), chatRequestMessage.getContent()));
        }
        // 不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));
        }
    }
}
