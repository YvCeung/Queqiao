package org.xiaoyu.queqiao.chatroom.server.session;

import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * @Description 聊天组实体类
 * @Author zy
 * @Date 2025/7/30 22:29
 **/
@Data
public class ChatGroup {
    // 聊天室名称
    private String name;
    // 聊天室成员
    private Set<String> members;

    public static final ChatGroup EMPTY_GROUP = new ChatGroup("empty", Collections.emptySet());

    public ChatGroup(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }
}
