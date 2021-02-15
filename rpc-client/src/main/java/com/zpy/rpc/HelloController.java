package com.zpy.rpc;

import com.zpy.rpc.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author zhao peng yu
 * @version 1.0
 * @date 2021/2/13 17:16
 */
@Component
public class HelloController {

    //通过注解远程调用 方法
    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}
