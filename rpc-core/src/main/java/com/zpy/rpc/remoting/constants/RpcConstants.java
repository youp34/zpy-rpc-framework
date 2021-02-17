package com.zpy.rpc.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author zhao peng yu
 */
public class RpcConstants {


    /**
     * 魔术
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    /**
     * 版本信息  用作校验
     */
    public static final byte VERSION = 1;
    /**
     * 长度
     */
    public static final byte TOTAL_LENGTH = 16;
    /**
     * 用作判断是否是请求类型  然后封装RpcMessage date
     */
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    /**
     * 用作心跳检测
     */
    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    /**
     * 请求头的长度
     */
    public static final int HEAD_LENGTH = 16;
    /**
     * 1M
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
