```
包体 : [magic:消息标签|4Byte][option:选项|1Byte][payloadLength:负载长度|动态字节][负载|payloadLength Byte][checkCode校验码|4Byte]
    
    负载 : [messageId:消息id|VarByte][headerOption:选项|1Byte][protocol:协议|VarByte][code:消息码|varByte][toMessage:响应|varByte][time:时间|varByte]
        [bodyLength:消息体长度|VarByte][body消息体|bodyLength Byte]
        [tailLength:消息体长度|VarByte][tail消息尾|tailLength Byte]

    magic : "tng."固定字节
    option : 
        000000[00]位 : 
            0[x] : 标识是否是 ping pong 消息, 1为是 ping pong, 0 为普通信息
            [x]1 : 0 为 ping 消息, 1 为 pong 消息
    
        00000[0]00位 : 加密标识位, 1 加密, 0 非加密
        0000[0]000位 : 校验标识位, 1 有校验码, 0 无校验码
        000[0]0000位 : 废字节标识位, 1 有废字节, 0 无废字节
    
    messageOption :
        000000[00] : 消息模式表示位 [01]为请求, [10]响应, [11]推送
        00000[0]00 : 是否有消息体
        0000[0]000 : 是否有消息尾部
    
```