package org.xiaoyu.queqiao.chatroom.server.service.impl;

import org.xiaoyu.queqiao.common.service.IUserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author zy
 * @Date 2025/7/28 23:37
 **/
public class UserServiceMemoryImpl implements IUserService {
    private static Map<String, String> allUserMap = new ConcurrentHashMap<>();

    static {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("zhaoliu", "123");
        allUserMap.put("qianqi", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
