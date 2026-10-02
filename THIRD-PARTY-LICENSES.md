# 第三方依赖许可证清单

TnyFramework 本体采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)。本文件记录本项目构建期与运行期直接引用的第三方组件各自的许可证，供再分发者履行声明义务时对照。清单依据 `gradle/dependency.gradle` 与 `gradle/publications.gradle` 中声明的坐标整理；各构件的权威许可证以该构件自身分发的 POM 与 LICENSE 文件为准，版本升级后请以升级时的构件元数据复核。

## 一、采用 Apache License 2.0 的组件

以下组件与本项目许可证相同，再分发时只需按 Apache-2.0 第 4 条保留其许可证与 NOTICE 内容：

| 组件 | 坐标 |
|---|---|
| Netty | io.netty:netty-* |
| Spring Boot / Spring Cloud / Spring Framework | org.springframework.*、org.springframework.cloud:* |
| Apache Log4j 2 | org.apache.logging.log4j:* |
| Jackson | com.fasterxml.jackson.*:* |
| Apache Commons 系列（codec、io、lang3、pool2、collections4、beanutils、configuration、dbcp2、crypto、math3） | org.apache.commons:*、commons-*:* |
| Guava | com.google.guava:guava |
| Caffeine | com.github.ben-manes.caffeine:caffeine |
| LMAX Disruptor | com.lmax:disruptor |
| cglib（nodep） | cglib:cglib-nodep |
| Groovy 4 | org.apache.groovy:* |
| MVEL 2 | org.mvel:mvel2 |
| Redisson | org.redisson:* |
| MongoDB Java Driver | org.mongodb:mongo-java-driver |
| jetcd | io.etcd:jetcd-core |
| jprotobuf（含 Gradle 预编译插件） | com.baidu:jprotobuf、gradle.plugin.com.baidu.jprotobuf:* |
| Druid | com.alibaba:druid |
| Nacos 客户端与 Spring Cloud Alibaba | com.alibaba.nacos:nacos-client、com.alibaba.cloud:* |
| Apache Kafka | org.apache.kafka:* |
| Apache ZooKeeper | org.apache.zookeeper:zookeeper |
| Apache POI | org.apache.poi:* |
| Apache SkyWalking 工具包 | org.apache.skywalking:* |
| HttpClient | org.apache.httpcomponents:httpclient |
| Joda-Time | joda-time:joda-time |
| Quartz | org.quartz-scheduler:quartz |
| Javassist | org.javassist:javassist（LGPL-2.1 与 Apache-2.0 双许可，按 Apache-2.0 使用） |
| spymemcached | com.whalin:Memcached-Java-Client |
| Byte Buddy | net.bytebuddy:* |
| AssertJ、Awaitility、Testcontainers 之外的测试框架类组件 | org.assertj:assertj-core、org.awaitility:awaitility 等 |
| Error Prone 注解 | com.google.errorprone:error_prone_annotations |
| Google API Client 与认证库 | com.google.api-client:*、com.google.auth:* |
| Spring Batch、Spring Retry | org.springframework.batch:*、org.springframework.retry:spring-retry |

## 二、采用其他宽松许可证的组件

| 组件 | 坐标 | 许可证 |
|---|---|---|
| Protocol Buffers Java | com.google.protobuf:protobuf-java | BSD-3-Clause |
| XStream | com.thoughtworks.xstream:xstream | BSD-3-Clause |
| SLF4J | org.slf4j:* | MIT |
| LuaJ | org.luaj:luaj-jse | MIT |
| Checker Qual | org.checkerframework:checker-qual | MIT |
| Jedis | redis.clients:jedis | MIT |
| JMH 基准框架（tny-bench 模块） | org.openjdk.jmh:* | GPL-2.0 with Classpath Exception |
| GraalVM 语言运行时 | org.graalvm.*:* | 双许可（UPL-1.0 与 GPL-2.0 with Classpath Exception），按 UPL-1.0 使用 |
| ICU4J | com.ibm.icu:icu4j | Unicode License v3（宽松许可证） |
| JUnit 5 平台 | org.junit.*:* | EPL-2.0 |
| AspectJ | org.aspectj:* | EPL-2.0（与 LGPL-2.1 双许可，按 EPL-2.0 使用） |
| Java EE / 注解 API | javax:javaee-api、javax.annotation:javax.annotation-api | 双许可（CDDL-1.1 与 GPL-2.0 with Classpath Exception），仅作编译期引用 |
| hutool | cn.hutool:hutool-core | Mulan PSL v2（木兰宽松许可证第 2 版） |

## 三、需要特别留意的组件

**MySQL Connector/J（mysql:mysql-connector-java）** 的官方许可证是 GPL-2.0 配合 Universal FOSS Exception。它作为独立构件被驱动使用不产生传染，但如果你把本框架连同该驱动合并为单一 fat-jar 再分发，合并产物将受 GPL 条件约束。依赖了它的模块仅位于 obsolete 目录下的历史缓存模块，正常集成路径不会引入该驱动。

**hutool（cn.hutool:hutool-core）** 自身采用 Mulan PSL v2。Mulan PSL v2 与 Apache-2.0 同属宽松许可证家族，二者义务互不冲突，各自保留自身许可证随构件分发即可，不需要任何特殊处理。

## 四、再分发义务摘要

1. 以标准 Gradle 或 Maven 坐标方式依赖本框架时，各第三方构件独立分发，各自的许可证义务由使用者在打包环节处理，本清单即为对照依据。
2. 若将本框架与依赖合并为 fat-jar（shadow jar）分发，合并产物内包含的每个 Apache-2.0 依赖都需要随附其 LICENSE 文本与 NOTICE 文件内容，EPL、UPL 等双许可组件需保留其许可声明；建议直接引用上表逐构件核对。
3. 本清单不覆盖测试与构建插件的传递依赖全集，发布合规要求严格时，应使用 license 报告任务从实际解析出的依赖树生成权威清单。
