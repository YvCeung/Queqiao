package org.xiaoyu.queqiao.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @Description SPI 加载服务实现类
 * @Author zy
 * @Date 2025/7/28 23:41
 **/
public class SpiServiceLoadUtil {

    /**
     * 获取某个 SPI 接口的第一个实现
     *
     * @param service SPI 接口类型
     * @param <S>     实现类型
     * @return 第一个实现类的实例
     */
    public static <S> S loadFirst(Class<S> service) {
        ServiceLoader<S> loader = ServiceLoader.load(service, findClassLoader());
        for (S impl : loader) {
            return impl;
        }
        throw new IllegalStateException("No implementation found for SPI: " + service.getName());
    }

    /**
     * 获取某个 SPI 接口的所有实现，返回 List
     *
     * @param service SPI 接口类型
     * @param <S>     实现类型
     * @return 所有实现类的实例列表
     */
    public static <S> List<S> loadAll(Class<S> service) {
        ServiceLoader<S> loader = ServiceLoader.load(service, findClassLoader());
        List<S> result = new ArrayList<>();
        for (S impl : loader) {
            result.add(impl);
        }
        return result;
    }


    /**
     * 获取当前线程的类加载器
     */
    private static ClassLoader findClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null ? contextClassLoader : SpiServiceLoadUtil.class.getClassLoader();
    }
}
