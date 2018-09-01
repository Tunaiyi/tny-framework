-- 3.0.x
Message {
    @ProtoExField(1) int id : 消息ID递增,标识消息的唯一性
    @ProtoExField(2) int protocol : 协议
    @ProtoExField(3) int code : 结果码, request时候为100
    @ProtoExField(4) Object body : 消息体, request时候为List
    @ProtoExField(5) int toMessage; 当前消息回应的消息ID, push 为 -1, request 为 0, response 为 对应messageID
    @ProtoExField(6) String sign; 签名, response时候为空
    @ProtoExField(7) long time; 请求ID
}

PingPong 编码
    option
        00000[ping][pong]0

-- 2.0.x
Spring profile 信息

suite.server
    SessionService
    ServerConfiguration
    SpringNetServer
    SpringServerContext

suite.scheduler

suite.game_auth
    GameTicketMaker
    AccountManager
    AccountService
    UserLoginAuthProvider
    UserReloginAuthProvider

suite.serve_auth
    ServeTicketMaker
    ServeAuthProvider

suite.base

suite.cache



suite.all