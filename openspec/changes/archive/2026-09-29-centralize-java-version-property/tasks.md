# Tasks

## 1. 配置收敛

- [x] 1.1 `gradle.properties`：在版本属性区（Dependencies Versions 段上方或 encoding 附近）添加 `javaVersion=21`，附注释"项目 Java 版本单一事实来源：toolchain 与 source/targetCompatibility 均引用此属性"
- [x] 1.2 `build.gradle`：javaProjects 块 toolchain 改为 `JavaLanguageVersion.of(javaVersion.toInteger())`；同块 `sourceCompatibility/targetCompatibility` 改为 `JavaVersion.toVersion(javaVersion)`
- [x] 1.3 `build.gradle`：gradleProjects 块 toolchain 同样改为属性引用

## 2. 验证

- [x] 2.1 `./gradlew javaToolchains -Dorg.gradle.java.home=<corretto-21>` 确认解析仍为 21（属性引用生效）
- [x] 2.2 `./gradlew compileJava compileTestJava compileGroovy --continue -Dorg.gradle.java.home=<corretto-21>` 全工程零失败
- [x] 2.3 `./gradlew :tny-game-net:test -Dorg.gradle.java.home=<corretto-21>` 全绿（35 例，含 Mockito 组）
- [x] 2.4 覆盖能力抽验：`./gradlew :tny-game-common-lang:compileJava -PjavaVersion=17 --dry-run` 或 javaToolchains 确认属性可被 -P 覆盖（仅验证解析，不实跑 17 编译）；结论记入变更目录
