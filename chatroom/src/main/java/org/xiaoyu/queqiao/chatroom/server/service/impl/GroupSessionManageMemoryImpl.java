package org.xiaoyu.queqiao.chatroom.server.service.impl;

import io.netty.channel.Channel;
import org.xiaoyu.queqiao.chatroom.server.factory.SingleSessionManageServiceFactory;
import org.xiaoyu.queqiao.chatroom.server.service.IGroupSessionManageService;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;
import org.xiaoyu.queqiao.chatroom.server.session.ChatGroup;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupSessionManageMemoryImpl implements IGroupSessionManageService {
    private final Map<String, ChatGroup> groupMap = new ConcurrentHashMap<>();

    @Override
    public ChatGroup createGroup(String name, Set<String> members) {
        ChatGroup group = new ChatGroup(name, members);
        return groupMap.putIfAbsent(name, group);
    }

    @Override
    public ChatGroup joinMember(String name, String member) {
        // 如果 map 中 存在该 key 且对应的 value 不为 null 则执行函数式接口
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().add(member);
            return value;
        });
    }

    @Override
    public ChatGroup removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }

    @Override
    public ChatGroup removeGroup(String name) {
        return groupMap.remove(name);
    }

    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, ChatGroup.EMPTY_GROUP).getMembers();
    }

    @Override
    public List<Channel> getMembersChannel(String name) {
        ISessionManangeService sessionManageMemoryImpl = SingleSessionManageServiceFactory.getSessionManageService("SessionManageMemoryImpl");

        return getMembers(name).stream()
                .map(member -> sessionManageMemoryImpl.getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String getServiceName() {
        return "GroupSessionManageMemoryImpl";
    }
}
