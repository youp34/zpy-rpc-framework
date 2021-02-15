package com.zpy.rpc.serviceImpl;

import com.zpy.rpc.Hello;
import com.zpy.rpc.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/2/13 17:21
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {

    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}

