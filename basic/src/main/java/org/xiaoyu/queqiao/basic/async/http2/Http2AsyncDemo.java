package org.xiaoyu.queqiao.basic.async.http2;

import okhttp3.Protocol;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author zy
 * @Date 2025/9/27 16:52
 **/
public class Http2AsyncDemo {
    public static void main(String[] args) {
        HttpCallback<Response> callback = new HttpCallback<Response>() {
            @Override
            public void onSuccess(Response result) {
               throw new IllegalArgumentException("Simulated exception in onSuccess");
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCancelled() {

            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("key", "value");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            Http2ClientUtil.doPost("https://www.apache.org/", params, headers, callback);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                System.out.println("Caught expected exception: " + e.getMessage());
                return;
            }
        }

    }
}
