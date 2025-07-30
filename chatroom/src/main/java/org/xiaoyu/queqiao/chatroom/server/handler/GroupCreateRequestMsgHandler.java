package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.GroupSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.IGroupSessionManageService;
import org.xiaoyu.queqiao.chatroom.server.session.ChatGroup;
import org.xiaoyu.queqiao.common.message.GroupCreateRequestMessage;
import org.xiaoyu.queqiao.common.message.GroupCreateResponseMessage;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/30 22:02
 **/
@ChannelHandler.Sharable
public class GroupCreateRequestMsgHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        IGroupSessionManageService groupSessionManageService =
                GroupSessionManageServiceFactory.getGroupSessionManageService("GroupSessionManageMemoryImpl");
        ChatGroup chatGroup = groupSessionManageService.createGroup(groupName, members);
        if (chatGroup != null) {
            // 发生成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            // 发送拉群消息
            List<Channel> channels = groupSessionManageService.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + groupName));
            }
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已经存在"));
        }
    }
}
