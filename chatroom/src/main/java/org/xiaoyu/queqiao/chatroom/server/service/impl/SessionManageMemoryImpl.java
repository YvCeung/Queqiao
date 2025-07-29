package org.xiaoyu.queqiao.chatroom.server.service.impl;

import io.netty.channel.Channel;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 会话管理内存方式实现
 * @Author zy
 * @Date 2025/7/29 22:09
 **/
public class SessionManageMemoryImpl implements ISessionManangeService {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    private final Map<Channel,Map<String,Object>> channelAttributesMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        // 暂不明确用途
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {

    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return null;
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {

    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }
}
