package org.xiaoyu.queqiao.chatroom.config;


import org.xiaoyu.queqiao.chatroom.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class SystemPropertiesConfig {
    static Properties properties;
    static {
        /**
         * 关于此API 的用法 @link: https://www.yuque.com/zyvae/note/osiw9mdsw0x7twi3 @
         */
        try (InputStream in = SystemPropertiesConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if(value.equals("Java")) {
            return Serializer.Algorithm.Java;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
}