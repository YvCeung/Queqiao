package org.xiaoyu.queqiao.basic.async.case2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Yu.Z
 * @create: 2025-09-11 15:13
 **/
public class MsgFutureEnv {

    private static Map<String, MessageFuture> futureMap = new ConcurrentHashMap<>();

    public static void put(String msgId, MessageFuture future){
        futureMap.put(msgId, future);
    }

    public static void setRes(Object res, String msgId){
        MessageFuture messageFuture = futureMap.get(msgId);
        if(messageFuture != null){
            messageFuture.getAsyncFuture().complete(res);
        }
    }

}
