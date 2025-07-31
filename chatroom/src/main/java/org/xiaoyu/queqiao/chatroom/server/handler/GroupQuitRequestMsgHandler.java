package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.GroupSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.session.ChatGroup;
import org.xiaoyu.queqiao.common.message.GroupJoinResponseMessage;
import org.xiaoyu.queqiao.common.message.GroupQuitRequestMessage;

@ChannelHandler.Sharable
public class GroupQuitRequestMsgHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        ChatGroup chatGroup = GroupSessionManageServiceFactory
                .getGroupSessionManageService("GroupSessionManageMemoryImpl")
                .removeMember(msg.getGroupName(), msg.getUsername());
        if (chatGroup != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "已退出群" + msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群不存在"));
        }
    }
}
