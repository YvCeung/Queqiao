package org.xiaoyu.queqiao.chatroom.server.factory;

import org.xiaoyu.queqiao.chatroom.server.service.IGroupSessionManageService;
import org.xiaoyu.queqiao.chatroom.server.service.ISessionManangeService;
import org.xiaoyu.queqiao.common.util.SpiServiceLoadUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 群组会话服务工厂类
 * @Author zy
 * @Date 2025/7/29 22:41
 **/
public class GroupSessionManageServiceFactory {
    private final static Map<String, IGroupSessionManageService> groupSessionManageServiceMap = new ConcurrentHashMap<>();


    public static IGroupSessionManageService getGroupSessionManageService(String serviceName) {
        IGroupSessionManageService groupSessionManageService = groupSessionManageServiceMap.get(serviceName);

        if (groupSessionManageService == null){
            List<IGroupSessionManageService> serviceList = SpiServiceLoadUtil.loadAll(IGroupSessionManageService.class);
            for (IGroupSessionManageService manangeService : serviceList) {
                groupSessionManageServiceMap.put(manangeService.getServiceName(), manangeService);
            }
        }
        return groupSessionManageServiceMap.get(serviceName);
    }

}
