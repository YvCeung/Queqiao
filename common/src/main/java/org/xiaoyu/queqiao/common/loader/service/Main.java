package org.xiaoyu.queqiao.common.loader.service;

import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<PayService> load = ServiceLoader.load(PayService.class);
        for (PayService payService : load) {
            payService.pay();
        }
    }
}
