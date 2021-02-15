package com.zpy.rpc.registry;

import com.zpy.rpc.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service discovery
 *
 * @author zhao peng yu
 */
@SPI
public interface ServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcServiceName rpc service name
     * @return service address
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
