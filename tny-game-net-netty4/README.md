### Message 封包

* 网络包封包
    ``` 
    [Head|4字节][Option|1字节][PayloadLength|4字节][PayLoad| PayloadLength 字节]
    ```
    * Head : "tny."
    * Option : 选项 [8位] [000(WasteBytesOption|1位)(EncryptOption|1位)(VerifyOption|1位)(Type|2位)]
        * Type :
            * 0 : 消息
            * 1 : Ping
            * 2 : Pong
            * WasteBytesOption : 标记是否有费字节 (1有,0没有)
            * EncryptOption : 标记是否有加密 (1有,0没有)
            * VerifyOption : 标记是否有校验码 (1有,0没有)
    * PayloadLength : PayLoad 长度
    * PayLoad : 内容

* PayLoad编码
    ```
    [AccessId|动态字节][Number|动态字节][WasteBytes][Message][WasteBytes][CheckCode|4字节]
    ```
    * AccessId : 访问Id
    * Number : 网络包序号(重连重置)
    * WasteBytes : 费字节 (可选)
    * Message : 消息
    * CheckCode : Message字节的校验码

* Message编码
    ```
    [MessageId|VarInt64][Option|1字节][ProtocolId|VarInt32][ResultCode|VarInt32][ToMessage|VarInt64][Time|VarInt64]
    {[ForwardHeaderLength|VarInt32][ForwardHeaderBytes|Length字节]}{[BodyLength|VarInt32][BodyBytes|Length字节]}
    ```
    * MessageId : 消息Id
    * Option : 选项 [8位] [0(ExistForwardHeader|1位)(Line|3位)(ExistBody|1位)(Mode|2位)]
        * ExistForwardHeader : 标记是否有转发Header (1有,0没有)
        * Line : 线路
        * ExistBody : 是否存在消息体 (1有,0没有)
            * 1 : 存在
            * 0 : 不存在
        * Mode : 消息模式
            * 0 : 请求消息
            * 1 : 响应消息
            * 2 : 推送消息
    * Protocol : 协议号
    * ResultCode : 状态码
    * ToMessage : 响应请求Id(响应消息有效)
    * Time : 发送时间
    * ForwardHeaderLength[可选] : 转发Header长度
    * ForwardHeaderBytes[可选] : 转发Header
    * BodyLength[可选] : 消息体长度
    * BodyBytes[可选] : 消息体

### RelayPack 封包

* 网络包封包
    ```
    [Head|3字节][PacketLength|4字节][PackId|4字节][Option|1字节][Time|8字节][PacketArguments|n字节]
    ``` 
    * Head : "rpk"
    * PacketLength : 包长度
    * PackId : 包Id
    * Option : [8位][000(mark|1位)(subId|4位)]
        * mark (1位) :
            * 0 : 标识 Link (转发连接) 相关消息
            * 1 : 标识 Tunnel (虚拟通道) 相关消息
            * subId (4位) : 子类型id
    * Time : 发包时间
    * PacketArguments : 转发包参数 protobuf
