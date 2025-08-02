package org.xiaoyu.queqiao.chatroom.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.chatroom.server.factory.SingleSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;

/**
 * 退出不是一个消息，是触发了一个事件: 又分正常退出(客户端channel.close)和异常退出(客户端直接程序关闭，服务端会获得一个异常事件)
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 当连接断开时触发 inactive 事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ISessionManangeService sessionManageMemoryImpl = SingleSessionManageServiceFactory.getSessionManageService("SessionManageMemoryImpl");
        sessionManageMemoryImpl.unbind(ctx.channel());
        log.debug("{} 已经断开", ctx.channel());
    }

    // 当出现异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ISessionManangeService sessionManageMemoryImpl = SingleSessionManageServiceFactory.getSessionManageService("SessionManageMemoryImpl");

        sessionManageMemoryImpl.unbind(ctx.channel());
        log.debug("{} 已经异常断开 异常是{}", ctx.channel(), cause.getMessage());
    }
}
