package org.xiaoyu.queqiao.basic.async.case1;

/**
 * @description:
 * @author: Yu.Z
 * @create: 2025-09-11 10:58
 **/
public class AsyncTask {


    void execute(AsyncCallback callback) {
        new Thread(() -> {
            Result asyncRes = getResult();
            callback.onComplete(asyncRes);
        }).start();
    }


    private static Result getResult() {
        Result result = new Result();
        result.setRes("success");
        return result;
    }
}
