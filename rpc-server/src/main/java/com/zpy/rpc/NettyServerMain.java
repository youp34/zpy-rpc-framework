package com.zpy.rpc;


import com.zpy.rpc.annotation.RpcScan;
import com.zpy.rpc.entity.RpcServiceProperties;
import com.zpy.rpc.remoting.transport.netty.server.NettyRpcServer;
import com.zpy.rpc.serviceImpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.zpy.rpc"})
public class NettyServerMain {
    public static void main(String[] args) {
        // 通过注解注册服务
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // 手动注册服务
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        nettyRpcServer.registerService(helloService2, rpcServiceProperties);
        nettyRpcServer.start();
    }
}
