package com.zpy.rpc.annotation;


import java.lang.annotation.*;

/**
 * RPC引用注释，自动连接服务实现类  用在服务调用者方
 *
 * @author zhao peng yu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * 调用服务的版本
     */
    String version() default "";

    /**
     * 调用服务的所在组
     */
    String group() default "";

}
