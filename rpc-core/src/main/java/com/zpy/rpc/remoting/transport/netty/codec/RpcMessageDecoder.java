package com.zpy.rpc.remoting.transport.netty.codec;

import com.zpy.rpc.compress.Compress;
import com.zpy.rpc.enums.CompressTypeEnum;
import com.zpy.rpc.enums.SerializationTypeEnum;
import com.zpy.rpc.extension.ExtensionLoader;
import com.zpy.rpc.remoting.constants.RpcConstants;
import com.zpy.rpc.remoting.dto.RpcMessage;
import com.zpy.rpc.remoting.dto.RpcRequest;
import com.zpy.rpc.remoting.dto.RpcResponse;
import com.zpy.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 *
 *
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p>
 *
 * @author zhao peng yu
 *
 *
 *
 * FixedLengthFrameDecoder：定长协议解码器，我们可以指定固定的字节数算一个完整的报文
 *
 * LineBasedFrameDecoder：  行分隔符解码器，遇到\n或者\r\n，则认为是一个完整的报文
 *
 * DelimiterBasedFrameDecoder：    分隔符解码器，与LineBasedFrameDecoder类似，只不过分隔符可以自己指定
 *
 * LengthFieldBasedFrameDecoder：长度编码解码器，将报文划分为报文头/报文体，根据报文头中的Length字段确定报文体的长度，因此报文提的长度是可变的
 *
 * JsonObjectDecoder：json格式解码器，当检测到匹配数量的"{" 、”}”或”[””]”时，则认为是一个完整的json对象或者json数组。
 *
 * 这些实现类，都只是将接收到的二进制数据，解码成包含完整报文信息的ByteBuf实例后，就直接交给了之后的ChannelInboundHandler处理。
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      最大帧长度。它决定了可以接收的数据的最大长度。如果超过，数据将被丢弃。
     * @param lengthFieldOffset   长度字段偏移。长度字段是跳过指定字节长度的字段。
     *                            Length field offset. The length field is the one that skips the specified length of byte.
     * @param lengthFieldLength   长度字段中的字节数。
     *                            The number of bytes in the length field.
     * @param lengthAdjustment    要添加到长度字段值的补偿值
     *                            The compensation value to add to the value of the length field
     * @param initialBytesToStrip 跳过的字节数。如果您需要接收所有的头+正文数据，这个值就是如果您只想接收正文数据，那么您需要跳过头消耗的字节数。
     *                            Number of bytes skipped.
     *                            If you need to receive all of the header+body data, this value is 0
     *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }

        }
        return decoded;
    }


    private Object decodeFrame(ByteBuf in) {
        // 先检查魔术
        checkMagicNumber(in);
        // 检查版本
        checkVersion(in);
        int fullLength = in.readInt();
        // build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        //-16
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // decompress the bytes
            String compressName = CompressTypeEnum.getName(compressType);
            //解压缩
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bs = compress.decompress(bs);
            // deserialize the object
            // 获取反序列化类型
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}] ", codecName);
            // 获取实例
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            // 判断消息类型   决定消息体的类型
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}
