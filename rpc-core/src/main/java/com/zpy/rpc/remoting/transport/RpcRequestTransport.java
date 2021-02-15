package com.zpy.rpc.remoting.transport;

import com.zpy.rpc.extension.SPI;
import com.zpy.rpc.remoting.dto.RpcRequest;

/**
 * send RpcRequest。
 *
 * @author zhao peng yu
 */
@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
