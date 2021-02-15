package com.zpy.rpc.loadbalance;

import com.zpy.rpc.extension.SPI;

import java.util.List;

/**
 * Interface to the load balancing policy
 *
 * @author zhao peng yu
 *
 * //负载均衡接口
 */
@SPI
public interface LoadBalance {
    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceAddresses Service address list
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
