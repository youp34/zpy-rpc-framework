package com.zpy.rpc.provider;

import com.zpy.rpc.entity.RpcServiceProperties;
import com.zpy.rpc.enums.RpcErrorMessageEnum;
import com.zpy.rpc.exception.RpcException;
import com.zpy.rpc.extension.ExtensionLoader;
import com.zpy.rpc.registry.ServiceRegistry;
import com.zpy.rpc.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhao peng yu
 *
 * 服务注册 发现 类
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    //存储服务名+对象
    private final Map<String, Object> serviceMap;
    //存储服务名
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;


    /**
     * 初始化
     */
    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    /**
     * 本地缓存表注册服务
     * @param service              service object
     * @param serviceClass         the interface class implemented by the service instance object
     * @param rpcServiceProperties service related attributes
     */
    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {
        String rpcServiceName = rpcServiceProperties.toRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, service);
        log.info("Add service: {} and interfaces:{}", rpcServiceName, service.getClass().getInterfaces());
    }

    /**
     * 本地缓存map获取服务
     * @param rpcServiceProperties service related attributes
     * @return
     */
    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, RpcServiceProperties.builder().group("").version("").build());
    }

    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            String serviceName = serviceRelatedInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);

            this.addService(service, serviceRelatedInterface, rpcServiceProperties);

            //注册到服务中心
            serviceRegistry.registerService(rpcServiceProperties.toRpcServiceName(), new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

}
