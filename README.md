# TnyFramework - 游戏服务器框架

[![License](https://img.shields.io/badge/license-Mulan%20PSL%20v2-blue.svg)](http://license.coscl.org.cn/MulanPSL2)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green.svg)](https://spring.io/projects/spring-boot)
[![Netty](https://img.shields.io/badge/Netty-4.1.104-red.svg)](https://netty.io/)

> 一个功能完善、高性能、模块化的 Java 游戏服务器框架

## 📖 目录

- [项目介绍](#项目介绍)
- [技术栈](#技术栈)
- [模块说明](#模块说明)
  - [基础设施层](#基础设施层)
  - [网络通信层](#网络通信层)
  - [编解码层](#编解码层)
  - [数据持久化层](#数据持久化层)
  - [表达式引擎](#表达式引擎)
  - [游戏业务层](#游戏业务层)
  - [分布式服务](#分布式服务)
  - [开发工具](#开发工具)
  - [Spring Boot Starter](#spring-boot-starter)
  - [监控与运维](#监控与运维)
- [快速开始](#快速开始)
- [许可证](#许可证)

---

## 项目介绍

TnyFramework 是一个企业级的游戏服务器框架，采用现代化的技术栈和模块化设计，为开发者提供从网络通信、数据存储、业务逻辑到分布式服务的完整解决方案。

### 核心特性

- ⚡ **高性能网络通信**: 基于 Netty 4 的异步 IO，支持百万级并发连接
- 🔐 **安全可靠**: 内置加密、校验、防嗅探机制
- 🧩 **模块化设计**: 50+ 模块，按需集成
- 🔄 **多实现支持**: 编解码、脚本引擎、数据存储多种实现可选
- 🌐 **分布式架构**: 内置服务发现、负载均衡、RPC 支持
- 📊 **可观测性**: 集成 Skywalking APM 分布式追踪
- 🚀 **Spring Boot 集成**: 提供开箱即用的 Starter

---

## 技术栈

| 技术 | 版本 | 说明 |
|-----|------|------|
| **Java** | 21 | 基础开发语言 |
| **Spring Boot** | 3.2.1 | 应用框架 |
| **Spring Cloud** | 2023.0.0 | 微服务框架 |
| **Netty** | 4.1.104 | 网络通信框架 |
| **Protobuf** | 3.22.2 | 序列化协议 |
| **Jackson** | 2.16.1 | JSON 处理 |
| **Groovy** | 4.0.17 | 脚本引擎 |
| **MongoDB** | - | NoSQL 数据库 |
| **Redisson** | - | Redis 客户端 |
| **Log4j2** | 2.22.1 | 日志框架 |
| **GraalVM** | 23.1.1 | 高性能运行时 |
| **Skywalking** | - | APM 监控 |

> **构建环境**：Gradle daemon（启动构建的 JVM）须运行于 JDK ≤21（Gradle 8.5 支持上限）；项目编译/测试经 toolchain 钉定 JDK 21，与 daemon 版本无关。

---

## 模块说明

以下模块清单与 `settings.gradle` 中未注释的 `include` 列表一一对应，按功能分层组织，均对应仓库根目录下现存源码。

> **非活跃范围**: `settings.gradle` 中已注释的模块（`tny-game-cache*`、`tny-game-asyndb`、`tny-game-zookeeper`、`tny-game-suite`、`tny-game-test-lua`）已停止维护，源码归档于 `obsolete/` 目录，不参与根构建，不属于活跃模块范围，请勿作为导航或依赖目标。

### 基础设施层

提供框架底层的通用工具和基础能力。

#### `tny-game-common-lang`
**通用语言工具库**
- 字符串处理、集合工具、对象工具
- 断言工具、类型转换
- 常用工具类封装

#### `tny-game-common-reflect`
**反射工具库**
- 高性能反射操作
- 类扫描与元数据解析
- 泛型类型解析
- 方法/字段快速访问

#### `tny-game-common-digest`
**加密摘要库**
- **对称加密**: AES, Blowfish
- **非对称加密**: RSA
- **摘要算法**: MD5, SHA
- **编码工具**: Base62, Binary
- **签名验证**: ParamsSigner, ParamsVerifiable

#### `tny-game-common-io`
**IO 工具库**
- 文件读写工具
- 流处理工具
- 配置文件加载
- 资源管理

#### `tny-game-common-lifecycle`
**生命周期管理**
- 组件生命周期管理
- 启动/停止顺序控制
- 依赖注入支持
- Unit 单元管理

#### `tny-game-common-scheduler`
**任务调度器**
- 定时任务调度
- 延迟任务执行
- 周期任务管理
- 线程池管理

#### `tny-game-scanning`
**类扫描库**
- 包路径扫描
- 注解扫描
- 类型过滤
- 快速类加载

#### `tny-game-loader`
**资源加载器**
- 游戏配置加载
- 静态数据加载
- Excel/JSON/XML 解析
- 热更新支持

#### `tny-game-bom`
**依赖版本清单 (Bill of Materials)**
- java-platform 聚合模块
- 统一约束全部活跃模块的发布版本
- 便于业务工程一键引入版本对齐的依赖

---

### 网络通信层

基于 Netty 的高性能网络通信模块。

#### `tny-game-net`
**网络框架核心**
- **消息处理**: Message, MessageHead, MessageBody
- **RPC 支持**: RpcContext, RpcInvokeCommand
- **会话管理**: Session, Certificate, Authentication
- **传输层**: Tunnel, Transport, Connection
- **集群支持**: Cluster, LoadBalancer
- **中继转发**: Relay, RelayTunnel
- **命令调度**: MessageDispatcher, CommandExecutor

#### `tny-game-net-netty4`
**Netty4 网络实现**
- **服务端**: NettyServerGuide, ServerTunnelFactory
- **客户端**: NettyClientGuide, ClientConnector
- **消息编解码**: NetPacketV1Encoder/Decoder
- **消息格式**:
  ```
  [Head|4字节][Option|1字节][PayloadLength|4字节][PayLoad|n字节]
  ```
- **安全特性**: 加密、校验、废字节防嗅探
- **心跳机制**: Ping/Pong
- **中继转发**: RelayPacket, RelayMessageHandler

#### `tny-game-net-netty4-codec-jprotobuf`
**Netty4 JProtobuf 消息体编解码**
- TypeProtobufMessageBodyCodec 消息体编解码
- ProtobufCodec / ProtobufRawType 类型支持
- 与 `tny-game-codec-jprotobuf` 配套接入网络层

#### `tny-game-net-netty4-codec-protoex`
**Netty4 ProtoEx 消息体编解码**
- ProtoExMessageBodyCodec 消息体编解码
- ProtoEx 协议接入网络层
- 与 `tny-game-codec-protoex` 配套使用

#### `tny-game-net-demo`
**网络框架示例**
- 服务端示例
- 客户端示例
- 协议定义示例
- 使用教程

#### `tny-game-net-test`
**网络测试工具**
- Mock 对象
- 单元测试基类
- 压力测试工具

---

### 编解码层

支持多种序列化协议的编解码模块。

#### `tny-game-codec`
**编解码抽象层**
- ObjectCodec 接口定义
- ObjectCodecFactory 工厂
- MimeType 支持
- 编解码适配器

#### `tny-game-codec-jackson`
**Jackson JSON 编解码**
- JSON 序列化/反序列化
- 自定义序列化器
- 日期格式处理
- 泛型支持

#### `tny-game-codec-jprotobuf`
**JProtobuf 编解码**
- Protobuf 序列化
- 类型化 Protobuf 支持
- 高性能编解码
- 跨语言兼容

#### `tny-game-codec-protoex`
**ProtoEx 扩展编解码**
- Protobuf 扩展协议
- 更灵活的序列化
- 动态类型支持

#### `tny-game-protoex`
**ProtoEx 协议库**
- ProtoEx 协议实现
- Reader/Writer
- 类型系统

#### `tny-game-protobuf`
**Protobuf 工具库**
- Protobuf 格式转换
- JSON/XML/HTML 输出
- CouchDB 格式支持

---

### 数据持久化层

提供统一的数据访问接口和多种存储实现。

#### `tny-game-data`
**数据访问抽象层**
- **实体管理**: EntityManager, EntityCacheManager
- **存储访问**: StorageAccessor, StorageAccessorFactory
- **缓存管理**: Cache, CacheKeyMaker
- **ID 转换**: EntityIdConverter
- **注解支持**: @Entity, @EntityKey

#### `tny-game-data-mongodb`
**MongoDB 存储实现**
- MongoStorageAccessor
- JSON 实体转换
- 文档增强
- 查询构建器

#### `tny-game-data-redisson`
**Redisson 存储实现**
- RedissonStorageAccessor
- Redis 数据结构支持
- 分布式锁
- 原子操作

#### `tny-game-mongodb`
**MongoDB 客户端封装**
- MongoDB 连接管理
- 配置自动化
- Spring Boot 集成

#### `tny-game-redisson`
**Redisson 客户端封装**
- Redisson 客户端管理
- 配置自动化
- Spring Boot 集成

---

### 表达式引擎

支持多种脚本语言的表达式计算引擎。

#### `tny-game-expr`
**表达式抽象层**
- Expr 接口定义
- ExprContext 上下文
- ExprHolder 持有器
- ExprHolderFactory 工厂

#### `tny-game-expr-mvel`
**MVEL 表达式引擎**
- MVEL 表达式支持
- MVEL 模板引擎
- 高性能计算
- 类型安全

#### `tny-game-expr-groovy`
**Groovy 脚本引擎**
- Groovy 脚本执行
- 动态编译
- Java 互操作
- 闭包支持

#### `tny-game-expr-graaljs`
**GraalJS 引擎**
- JavaScript 执行
- GraalVM 高性能
- Polyglot 支持

#### `tny-game-expr-luaj`
**Lua 脚本引擎**
- Lua 脚本执行
- LuaJ 实现
- 轻量级嵌入

#### `tny-game-expr-jsr223`
**JSR223 通用引擎**
- JSR223 规范实现
- 多脚本引擎支持
- 统一接口

---

### 游戏业务层

游戏特定的业务组件。

#### `tny-game-basics`
**游戏基础组件**
- **物品系统**: Item, ItemModel, Behavior
- **模型系统**: Mould, Feature
- **升级系统**: Upgrade, Experience
- **异常处理**: GameException, ResultCode

#### `tny-game-actor`
**Actor 模型**
- Actor 并发模型
- 消息传递
- 状态管理
- 生命周期

#### `tny-game-oplog`
**操作日志**
- 玩家行为日志
- 审计日志
- 日志格式化
- 异步写入

#### `tny-game-oplog-log4j`
**Log4j2 日志实现**
- Log4j2 Appender
- 滚动策略
- 日志分类

---

### 分布式服务

分布式系统支持组件。

#### `tny-game-namnspace`
**命名空间/服务发现**
- **服务发现**: NamespaceExplorer
- **一致性哈希**: NodeHashing, HashingPublisher
- **分片支持**: Sharding
- **租约管理**: Lessee
- **节点监听**: NameNodesWatcher

#### `tny-game-namnspace-etcd`
**Etcd 服务发现实现**
- Etcd 客户端集成
- 服务注册/注销
- 健康检查
- 配置中心

#### `tny-game-rpc`
**RPC 框架**
- RPC 调用
- 负载均衡
- 超时控制
- 异常处理

---

### 开发工具

开发辅助工具模块。

#### `tny-game-doc`
**文档生成器**
- 协议文档生成
- DTO 文档生成
- 枚举文档生成
- 多语言输出

#### `tny-game-doc-annotation`
**文档注解**
- @ClassDoc, @FunDoc, @VarDoc
- @DTODoc, @IDDoc
- @DocTag, @Export

#### `tny-game-doc-gradle`
**Gradle 文档插件**
- Gradle 任务集成
- 自动化文档生成
- 构建时生成

#### `tny-game-boot`
**启动引导**
- 应用启动
- 配置加载
- 环境初始化

#### `tny-game-boot-log4j2`
**Log4j2 启动支持**
- Spring 上下文集成
- 环境变量查找
- 动态配置

#### `tny-game-tester`
**测试工具**
- 测试基类
- Mock 工具
- 断言扩展

---

### Spring Boot Starter

开箱即用的 Spring Boot 集成模块。

#### `tny-game-starter-basics`
**基础 Starter**
- 游戏基础组件自动配置
- Bean 注册
- 默认配置

#### `tny-game-starter-codec`
**编解码 Starter**
- Jackson 自动配置
- Protobuf 自动配置
- 编解码器注册

#### `tny-game-starter-data`
**数据访问 Starter**
- EntityManager 自动配置
- 缓存配置
- 事务支持

#### `tny-game-starter-mongodb`
**MongoDB Starter**
- MongoDB 客户端配置
- 连接池配置
- 健康检查

#### `tny-game-starter-redisson`
**Redisson Starter**
- Redisson 客户端配置
- 分布式锁配置
- 缓存配置

#### `tny-game-starter-namnspace`
**命名空间 Starter**
- 服务发现配置
- Etcd 集成
- 自动注册

#### `tny-game-starter-net-netty4`
**Netty4 网络 Starter**
- 服务端自动配置
- 客户端自动配置
- 编解码器配置
- 端口配置

#### `tny-game-starter-net-apm-skywalking`
**Skywalking APM Starter**
- Skywalking 集成
- 分布式追踪
- RPC 监控
- 性能分析

---

### 监控与运维

#### 网络通信协议

**Message 消息封包格式**:
```
[Head|4字节][Option|1字节][PayloadLength|4字节][PayLoad|n字节]
```

**Option 字节说明**:
- Type (2位): 消息类型 (0=消息, 1=Ping, 2=Pong)
- VerifyOption (1位): 是否校验
- EncryptOption (1位): 是否加密
- WasteBytesOption (1位): 是否有废字节

**PayLoad 编码**:
```
[AccessId|VarInt64][Number|VarInt32][WasteBytes][Message][CheckCode|4字节]
```

**Message 编码**:
```
[MessageId|VarInt64][Option|1字节][ProtocolId|VarInt32]
[ResultCode|VarInt32][ToMessage|VarInt64][Time|VarInt64]
[ForwardHeader][Body]
```

详细协议说明请参考: [tny-game-net-netty4/README.md](tny-game-net-netty4/README.md)

---

## 快速开始

### 环境要求

- JDK 21+
- Gradle 7.0+
- MongoDB 4.0+ (可选)
- Redis 5.0+ (可选)
- Etcd 3.4+ (可选)

### 构建项目

```bash
# 克隆项目
git clone <repository-url>
cd tny-framework

# 编译
./gradlew build

# 跳过测试编译
./gradlew build -x test

# 发布到本地 Maven 仓库
./gradlew publishToMavenLocal
```

### 使用示例

#### 1. 添加依赖

**Gradle**:
```gradle
dependencies {
    implementation 'com.tny.game:tny-game-starter-net-netty4:${projectVersion}'
    implementation 'com.tny.game:tny-game-starter-codec:${projectVersion}'
    implementation 'com.tny.game:tny-game-starter-data:${projectVersion}'
}
```

#### 2. 配置文件

**application.yml**:
```yaml
tny:
  net:
    server:
      port: 8080
      name: game-server
    codec:
      type: protobuf
  data:
    mongodb:
      uri: mongodb://localhost:27017/gamedb
    redis:
      address: redis://127.0.0.1:6379
```

#### 3. 创建协议处理器

```java
@RpcController
public class PlayerController {
    
    @RpcRequest(1001) // 协议号
    public LoginResponse login(LoginRequest request, NetTunnel tunnel) {
        // 业务逻辑
        return new LoginResponse(...);
    }
}
```

#### 4. 启动应用

```java
@SpringBootApplication
public class GameServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameServerApplication.class, args);
    }
}
```

更多示例请参考: [tny-game-net-demo](tny-game-net-demo/)

---

## 模块依赖关系

```
基础设施层 (common-*)
    ↓
工具层 (expr, codec, loader, scanning)
    ↓
网络层 (net, net-netty4)
    ↓
数据层 (data, data-mongodb, data-redisson)
    ↓
业务层 (basics, actor, oplog)
    ↓
分布式层 (namnspace, namnspace-etcd, rpc)
    ↓
Starter 层 (starter-*)
```

---

## 性能特性

- **高并发**: 单机支持 10W+ 在线连接
- **低延迟**: 平均响应时间 < 10ms
- **高吞吐**: 单机处理 10W+ QPS
- **内存优化**: ByteBuf 池化，对象复用
- **异步 IO**: 基于 Netty 的 Reactor 模型

---

## 适用场景

- ✅ MMORPG (大型多人在线角色扮演游戏)
- ✅ 卡牌/策略类游戏
- ✅ 实时对战游戏
- ✅ 棋牌游戏
- ✅ SLG (策略类游戏)
- ✅ 其他需要高性能网络通信的在线游戏

---

## 贡献指南

欢迎贡献代码！请遵循以下步骤:

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Java 编码规范
- 添加必要的注释
- 编写单元测试
- 保持代码简洁

---

## 许可证

本项目采用 [木兰宽松许可证, 第2版 (Mulan PSL v2)](http://license.coscl.org.cn/MulanPSL2) 开源协议。

Copyright (c) 2020 Tunaiyi

---

## 联系方式

- 问题反馈: [Issues](../../issues)
- 文档: [Wiki](../../wiki)

---

## 致谢

感谢以下开源项目:

- [Netty](https://netty.io/) - 高性能网络框架
- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [Protobuf](https://developers.google.com/protocol-buffers) - 序列化协议
- [MongoDB](https://www.mongodb.com/) - NoSQL 数据库
- [Redis](https://redis.io/) - 内存数据库
- [Etcd](https://etcd.io/) - 分布式配置中心