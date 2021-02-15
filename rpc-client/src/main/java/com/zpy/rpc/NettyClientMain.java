package com.zpy.rpc;

import com.zpy.rpc.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/2/13 17:17
 */
@RpcScan(basePackage = {"com.zpy.rpc"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        //基于spring进行容器扫描启动等事项
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        //远程调用服务
        helloController.test();
    }
}

