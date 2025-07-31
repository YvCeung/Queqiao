package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.GroupSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.IGroupSessionManageService;
import org.xiaoyu.queqiao.common.message.GroupChatRequestMessage;
import org.xiaoyu.queqiao.common.message.GroupChatResponseMessage;

import java.util.List;

@ChannelHandler.Sharable
public class GroupChatRequestMsgHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        // 群管理器
        IGroupSessionManageService groupSessionManageService =
                GroupSessionManageServiceFactory.getGroupSessionManageService("GroupSessionManageMemoryImpl");
        List<Channel> membersChannel = groupSessionManageService.getMembersChannel(msg.getGroupName());


        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
