package org.xiaoyu.queqiao.common.loader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.common.loader.service.PayService;

@Slf4j
public class WXPay implements PayService {
    @Override
    public void pay() {
        log.info("WX pay begin...");
    }
}
