package com.zpy.rpc.serviceImpl;

import com.zpy.rpc.Hello;
import com.zpy.rpc.HelloService;
import com.zpy.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/2/13 17:21
 */
@Slf4j
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
