package com.zpy.rpc.registry.zk;

import com.zpy.rpc.enums.RpcErrorMessageEnum;
import com.zpy.rpc.exception.RpcException;
import com.zpy.rpc.extension.ExtensionLoader;
import com.zpy.rpc.loadbalance.LoadBalance;
import com.zpy.rpc.registry.ServiceDiscovery;
import com.zpy.rpc.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * service discovery based on zookeeper
 *
 * @author shuang.kou
 * @createTime 2020年06月01日 15:16:00
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡寻找随机一个服务地址进行连接
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        //对服务地址进行分割  host  port
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
