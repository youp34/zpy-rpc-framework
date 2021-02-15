package com.zpy.rpc.remoting.transport.socket;

import com.zpy.rpc.config.CustomShutdownHook;
import com.zpy.rpc.entity.RpcServiceProperties;
import com.zpy.rpc.factory.SingletonFactory;
import com.zpy.rpc.provider.ServiceProvider;
import com.zpy.rpc.provider.ServiceProviderImpl;
import com.zpy.rpc.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static com.zpy.rpc.remoting.transport.netty.server.NettyRpcServer.PORT;

/**
 * @author shuang.kou
 * @createTime 2020年05月10日 08:01:00
 */
@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;


    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public void registerService(Object service) {
        serviceProvider.publishService(service);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            //获取本机的地址
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            //当服务器关闭时，执行一些操作，例如注销所有服务
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                //提交任务给每一个线程
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }

}
