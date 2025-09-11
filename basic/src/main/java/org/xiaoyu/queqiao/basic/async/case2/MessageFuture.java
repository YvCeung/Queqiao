package org.xiaoyu.queqiao.basic.async.case2;

import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * @description:
 * @author: Yu.Z
 * @create: 2025-09-11 15:08
 **/
@Data
public class MessageFuture {
    private CompletableFuture<Object> asyncFuture = new CompletableFuture<>();

    public Object get() throws Exception {
        return asyncFuture.get();
    }
}
