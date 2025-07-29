package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.chatroom.server.factory.SessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;
import org.xiaoyu.queqiao.common.message.LoginRequestMessage;
import org.xiaoyu.queqiao.common.message.LoginResponseMessage;
import org.xiaoyu.queqiao.common.service.IUserService;
import org.xiaoyu.queqiao.common.util.SpiServiceLoadUtil;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/28 23:32
 **/
@ChannelHandler.Sharable
public class LoginRequestMsgHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        IUserService userService = SpiServiceLoadUtil.loadFirst(IUserService.class);
        boolean login = userService.login(username, password);

        if (login) {
            ISessionManangeService sessionManageMemoryImpl = SessionManageServiceFactory.getSessionManageService("SessionManageMemoryImpl");
            // 绑定用户名和channel的关系，用于消息发送
            sessionManageMemoryImpl.bind(ctx.channel(), username);
            LoginResponseMessage loginResponseMessage = new LoginResponseMessage(true, null);
            ctx.writeAndFlush(loginResponseMessage);
        }else {
            LoginResponseMessage loginResponseMessage = new LoginResponseMessage(false, "用户名或密码错误");
            ctx.writeAndFlush(loginResponseMessage);

        }
    }
}
