package com.zpy.rpc;

import com.zpy.rpc.entity.RpcServiceProperties;
import com.zpy.rpc.remoting.transport.socket.SocketRpcServer;
import com.zpy.rpc.serviceImpl.HelloServiceImpl;

/**
 * 测试类
 */
public class SocketServerMain {
    public static void main(String[] args) {
        //创建实例对象
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        socketRpcServer.registerService(helloService, rpcServiceProperties);
        socketRpcServer.start();
    }
}
