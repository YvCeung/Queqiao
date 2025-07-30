package org.xiaoyu.queqiao.chatroom.server.factory;

import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;
import org.xiaoyu.queqiao.common.util.SpiServiceLoadUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 单会话服务工厂类
 * @Author zy
 * @Date 2025/7/29 22:41
 **/
public class SingleSessionManageServiceFactory {
    private final static Map<String, ISessionManangeService> sessionManangeServiceMap = new ConcurrentHashMap<>();


    public static ISessionManangeService getSessionManageService(String serviceName) {
        ISessionManangeService sessionManangeService = sessionManangeServiceMap.get(serviceName);

        if (sessionManangeService == null){
            List<ISessionManangeService> serviceList = SpiServiceLoadUtil.loadAll(ISessionManangeService.class);
            for (ISessionManangeService manangeService : serviceList) {
                sessionManangeServiceMap.put(manangeService.getServiceName(), manangeService);
            }
        }
        return sessionManangeServiceMap.get(serviceName);
    }

}
