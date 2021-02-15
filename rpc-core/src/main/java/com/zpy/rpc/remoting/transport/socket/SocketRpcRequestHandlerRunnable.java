package com.zpy.rpc.remoting.transport.socket;

import com.zpy.rpc.factory.SingletonFactory;
import com.zpy.rpc.remoting.dto.RpcRequest;
import com.zpy.rpc.remoting.dto.RpcResponse;
import com.zpy.rpc.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author zhao peng yu
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;


    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            //读取request对象信息
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            //处理方法
            Object result = rpcRequestHandler.handle(rpcRequest);
            //返回信息   RpcResponse对象进行封装
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            //清空缓存区
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }

}
