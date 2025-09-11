package org.xiaoyu.queqiao.basic.async;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @description: 异步调用演示Case
 * @author: Yu.Z
 * @create: 2025-09-11 10:55
 **/
@Slf4j
public class AsyncDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Result result = testSync();
        log.info("同步响应结果:{}", result.getRes());

        CompletableFuture<Result> futureResult = testAsyncFuture();

        testAsyncCallback();
        log.info("主线程继续向下执行~~");
        TimeUnit.SECONDS.sleep(1); // 保证所有结果处理完毕


        log.info("主线程从异步 Future 中获取结果: {}", futureResult.get().getRes());
    }


    public static void testAsyncCallback() {
        new AsyncTask().execute(new AsyncCallback() {
            @Override
            public void onComplete(Result result) {
                try {
                    // 模拟异步耗时
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                log.info("异步 Callback 获取结果: {}", result.getRes());
            }
        });
    }

    public static CompletableFuture<Result> testAsyncFuture() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {

            }
            Result result = getResult();
            log.info("异步 Future 获取结果: {}", result.getRes());
            return result;
        });
    }




    public static Result testSync() {
        return getResult();
    }

    private static Result getResult() {
        Result result = new Result();
        result.setRes("success");
        return result;
    }
}
