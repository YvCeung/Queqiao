package org.xiaoyu.queqiao.basic.async.case2;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Yu.Z
 * @create: 2025-09-11 15:07
 **/
public class AsyncDemo2 {
    public static void main(String[] args) throws Exception {
        MessageFuture messageFuture = new MessageFuture();

        MsgFutureEnv.put("123", messageFuture);


        //模拟网络异步执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                    // 接收到异步响应后设置结果
                    MsgFutureEnv.setRes("收到消息", "123");
                } catch (InterruptedException e) {

                }
            }
        }).start();

        //阻塞等待结果
        String o = (String)messageFuture.get();
        System.out.println(o);
    }
}
