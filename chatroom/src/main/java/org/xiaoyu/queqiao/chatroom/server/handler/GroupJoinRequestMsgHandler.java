package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.GroupSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.session.ChatGroup;
import org.xiaoyu.queqiao.common.message.GroupJoinRequestMessage;
import org.xiaoyu.queqiao.common.message.GroupJoinResponseMessage;

@ChannelHandler.Sharable
public class GroupJoinRequestMsgHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        // 群管理器
        ChatGroup chatGroup = GroupSessionManageServiceFactory
                .getGroupSessionManageService("GroupSessionManageMemoryImpl")
                .joinMember(msg.getGroupName(), msg.getUsername());

        if (chatGroup != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群加入成功"));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群不存在"));
        }
    }
}
