// package com.tny.game.net.message.protoex;
//
// import com.tny.game.common.unit.annotation.Unit;
// import com.tny.game.net.message.*;
// import com.tny.game.net.message.common.CommonMessage;
// import com.tny.game.net.transport.*;
//
// @Unit
// public class ProtoExMessageFactory<UID> implements MessageFactory<UID> {
//
//     private final Certificate<UID> UNLOGIN_CERTIFICATE = Certificates.createUnautherized();
//
//     @Override
//     public NetMessage<UID> create(long id, MessageContext<UID> context, Certificate<UID> certificate) {
//         return new CommonMessage<>(certificate, new ProtoExMessageHead(id, context), context.getBody());
//     }
//
//     @Override
//     public NetMessage<UID> create(NetMessageHead header, Object body) {
//         return new CommonMessage<>(UNLOGIN_CERTIFICATE, header, body);
//     }
//
// }
