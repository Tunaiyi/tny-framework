// package com.tny.game.net.message.protoex;
//
// import com.google.common.base.MoreObjects;
// import com.tny.game.net.message.*;
// import com.tny.game.net.transport.*;
// import com.tny.game.protoex.annotations.*;
//
// import java.util.Objects;
//
// @ProtoEx(ProtoExCodec.MESSAGE_HEAD_ID)
// public class ProtoExMessageHead extends AbstractNetMessageHead {
//
//     @ProtoExField(1)
//     private long id;
//
//     @ProtoExField(2)
//     private int protocol = -1;
//
//     @ProtoExField(3)
//     private int code;
//
//     @ProtoExField(4)
//     private long toMessage;
//
//     @ProtoExField(5)
//     private long time = -1;
//
//     @Packed(false)
//     @ProtoExField(value = 7, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
//     private Object attachment;
//
//     public ProtoExMessageHead() {
//     }
//
//     public ProtoExMessageHead(long id, MessageContext<?> context) {
//         super(context.getMode());
//         this.id = id;
//         this.protocol = context.getProtocolId();
//         this.code = context.getCode();
//         this.toMessage = context.getToMessage();
//         this.time = System.currentTimeMillis();
//         this.attachment = context.getAttachment();
//     }
//
//     @Override
//     public long getId() {
//         return this.id;
//     }
//
//     @Override
//     public int getCode() {
//         return this.code;
//     }
//
//     @Override
//     public long getToMessage() {
//         return toMessage;
//     }
//
//     @Override
//     public long getTime() {
//         return this.time;
//     }
//
//     @Override
//     public int getProtocolNumber() {
//         return this.protocol;
//     }
//
//     @Override
//     protected Object getAttachment() {
//         return attachment;
//     }
//
//     @Override
//     public String toString() {
//         return MoreObjects.toStringHelper(this)
//                 .add("id", id)
//                 .add("protocol", protocol)
//                 .add("code", code)
//                 .add("toMessage", toMessage)
//                 .add("time", time)
//                 .add("attachment", attachment)
//                 .toString();
//     }
//
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (!(o instanceof ProtoExMessageHead)) return false;
//         ProtoExMessageHead that = (ProtoExMessageHead) o;
//         return getId() == that.getId() &&
//                 protocol == that.protocol &&
//                 getCode() == that.getCode() &&
//                 getToMessage() == that.getToMessage() &&
//                 getTime() == that.getTime() &&
//                 Objects.equals(getAttachment(), that.getAttachment());
//     }
//
//     @Override
//     public int hashCode() {
//         return Objects.hash(getId(), protocol, getCode(), getToMessage(), getTime(), getAttachment());
//     }
//
//     ProtoExMessageHead setId(long id) {
//         this.id = id;
//         return this;
//     }
//
//     ProtoExMessageHead setProtocol(int protocol) {
//         this.protocol = protocol;
//         return this;
//     }
//
//     ProtoExMessageHead setCode(int code) {
//         this.code = code;
//         return this;
//     }
//
//     ProtoExMessageHead setToMessage(long toMessage) {
//         this.toMessage = toMessage;
//         return this;
//     }
//
//     ProtoExMessageHead setTime(long time) {
//         this.time = time;
//         return this;
//     }
//
//     ProtoExMessageHead setAttachment(Object attachment) {
//         this.attachment = attachment;
//         return this;
//     }
//
// }