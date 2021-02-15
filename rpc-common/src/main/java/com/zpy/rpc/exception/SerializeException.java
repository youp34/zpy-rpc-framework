package com.zpy.rpc.exception;

/**
 * @author zhao peng yu
 * 自定义序列化异常
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
