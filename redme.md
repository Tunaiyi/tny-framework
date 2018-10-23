```
4.0 支持计划

修改数据包封包结构.
    
    原来方案:
    [包头|4字节][option|1字节][消息长度|4字节][消息体]
    
    更改方案 :
    [包头|4字节][Option][Payload长度|4字节]
    PayLoad {
        [accessId|动态字节][number|动态字节][time|动态字节]
        [wasteBytes][Body][wasteBytes]
    }
    [checkcode|4字节]
    
        Head : 消息头, 由默认或自定义的 key value 组成
        id : 消息ID
        protocol : 协议号
        time : 请求时间
        toMessage : 响应消息
        其他自定义
    
    Payload : 消息载体,由MessageBody编码后的二进制
        MessageBody {
            code : 结果
            body : 消息体
        }
        
    Message对象最终由Head与Payload内容组成
    
```