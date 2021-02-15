package com.zpy.rpc;

import com.zpy.rpc.entity.RpcServiceProperties;
import com.zpy.rpc.proxy.RpcClientProxy;
import com.zpy.rpc.remoting.transport.RpcRequestTransport;
import com.zpy.rpc.remoting.transport.socket.SocketRpcClient;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/2/13 17:17
 */
public class SocketClientMain {
    public static void main(String[] args) {
        //定义一个传输对象
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        //定义服务配置
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        //创建代理调用
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //RPC调用远程方法
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}

