package com.zpy.rpc.annotation;


import java.lang.annotation.*;

/**
 * RPC服务批注，在服务实现类上标记 用在服务提供者方
 *
 * @author zhao peng yu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务版本
     */
    String version() default "";

    /**
     * 服务组
     */
    String group() default "";

}
