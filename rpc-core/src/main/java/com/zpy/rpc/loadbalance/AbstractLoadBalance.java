package com.zpy.rpc.loadbalance;

import java.util.List;

/**
 * 负载均衡抽象方法
 *
 * @author zhao peng yu
 * 抽象方法继承 负载均衡接口
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        //如果服务地址list只有一个则直接返回这一个地址否则调用
        // 抽象方法doSelect进行选择  -》1.一致性hash  2. 随机random
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcServiceName);
    }

    protected abstract String doSelect(List<String> serviceAddresses, String rpcServiceName);

}
