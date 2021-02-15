package com.zpy.rpc.registry;

import com.zpy.rpc.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service registration
 *
 * @author zhao peng yu
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
