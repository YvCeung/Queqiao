package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xiaoyu.queqiao.common.message.LoginRequestMessage;
import org.xiaoyu.queqiao.common.message.LoginResponseMessage;
import org.xiaoyu.queqiao.common.service.IUserService;
import org.xiaoyu.queqiao.common.util.SpiServiceLoadUtil;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/28 23:32
 **/
public class LoginRequestMsgHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        IUserService userService = SpiServiceLoadUtil.loadFirst(IUserService.class);
        boolean login = userService.login(username, password);

        if (login) {
            LoginResponseMessage loginResponseMessage = new LoginResponseMessage(true, null);
            ctx.writeAndFlush(loginResponseMessage);
        }else {
            LoginResponseMessage loginResponseMessage = new LoginResponseMessage(false, "用户名或密码错误");
            ctx.writeAndFlush(loginResponseMessage);

        }
    }
}
