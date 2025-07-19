package org.xiaoyu.queqiao.basic.protocol.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.xiaoyu.queqiao.common.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @Description 这是一个演示自定义协议进行解码编码的case
 * 无状态可以被共享，因为没有去存储数据供其他地方使用，但是不能加@Sharable注解，因为ByteToMessageCodec是netty提供的一个不支持Sharable注解
 * 如下：
     ByteToMessageCodec
     * protected ByteToMessageCodec(boolean preferDirect) {
     *         this.decoder = new NamelessClass_1();
     *         this.ensureNotSharable();
     *         this.outboundMsgMatcher = TypeParameterMatcher.find(this, ByteToMessageCodec.class, "I");
     *         this.encoder = new Encoder(preferDirect);
     *     }
 * @Author zy
 * @Date 2025/7/6 17:42
 **/
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * encode这个方法，会在出站的时候被调用。下面演示的这些步骤，是业界通用的协议设计步骤。包含：
     * ● 魔数，用来在第一时间判定是否是无效数据包
     * ● 版本号，可以支持协议的升级
     * ● 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
     * ● 指令类型，是登录、注册、单聊、群聊... 跟业务相关
     * ● 请求序号，为了双工通信，提供异步能力
     * ● 正文长度
     * ● 消息正文
     *
     * @param ctx
     * @param msg
     * @param out 是由netty已经创建好的，我们只需要按照自定义协议的格式，将message转换成byteBuf就可以
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(0);
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
    }

    /**
     * 同理，decode的解析逻辑需要按照上面encode的逻辑进行一步步的解析
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
