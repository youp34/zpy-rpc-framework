package com.zpy.rpc.remoting.dto;

import com.zpy.rpc.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

/**
 * @author zhao peng yu
 * 请求数据格式
 *
 * 最后封装成RpcMessage的data
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] parameters;
    /**
     * 参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 版本号
     */
    private String version;
    /**
     * 组
     */
    private String group;

    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder().serviceName(this.getInterfaceName())
                .version(this.getVersion())
                .group(this.getGroup()).build();
    }
}
