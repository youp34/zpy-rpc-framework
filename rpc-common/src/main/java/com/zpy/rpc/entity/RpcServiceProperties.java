package com.zpy.rpc.entity;

import lombok.*;

/**
 * @author zhao peng yu
 * 服务注册 格式
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {
    /**
     * 版本
     */
    private String version;
    /**
     * 组
     */
    private String group;
    private String serviceName;

    public String toRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
