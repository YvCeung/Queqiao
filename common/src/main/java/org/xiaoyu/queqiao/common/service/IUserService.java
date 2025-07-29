package org.xiaoyu.queqiao.common.service;

/**
 * @Description 用户服务
 * @Author zy
 * @Date 2025/7/28 23:36
 **/
public interface IUserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 true, 否则返回 false
     */
    boolean login(String username, String password);
}
