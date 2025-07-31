package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.GroupSessionManageServiceFactory;
import org.xiaoyu.queqiao.common.message.GroupMembersRequestMessage;
import org.xiaoyu.queqiao.common.message.GroupMembersResponseMessage;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestMsgHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionManageServiceFactory
                .getGroupSessionManageService("GroupSessionManageMemoryImpl")
                .getMembers(msg.getGroupName());
        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
