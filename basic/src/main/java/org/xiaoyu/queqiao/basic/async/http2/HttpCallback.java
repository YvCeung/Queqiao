package org.xiaoyu.queqiao.basic.async.http2;

/**
 * @Description
 * @Author zy
 * @Date 2025/9/27 16:44
 **/
/**
 * The interface HttpCallback.
 *
 * @param <T> the type parameter
 */
public interface HttpCallback<T> {

    /**
     * Called when the HTTP request is successful.
     *
     * @param result the result of the HTTP request
     */
    void onSuccess(T result);

    /**
     * Called when the HTTP request fails.
     *
     * @param e the exception that occurred during the HTTP request
     */
    void onFailure(Throwable e);

    /**
     * Called when the HTTP request is cancelled.
     */
    void onCancelled();
}
