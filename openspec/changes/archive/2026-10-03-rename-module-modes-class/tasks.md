# Tasks

## 1. 基线抓样

- [x] 1.1 无前置确认（本变更直接在 rename-module-modes-plugin 落地后的现名现场操作）。抓基线存本变更目录 baseline/：`./gradlew tasks --all --console=plain | grep -v -e '^> Task :' -e 'actionable tasks:' -e '^BUILD SUCCESSFUL in ' -e '^Starting a Gradle Daemon' -e 'Cannot run program' -e '^> Configure project :'` 的有序任务清单段（捕获口径：正常 locale、不注入 JAVA_TOOL_OPTIONS，按前两本改名变更确立的判据执行）；`./gradlew projects -q --console=plain` 评估输出记录为绿。

## 2. 改名实施

- [x] 2.1 `buildSrc/src/main/groovy/tny/convention/ModuleModes.groovy` 以 git mv 移名 `ModuleSetting.groovy`；类内四处随改——第 9 行类声明、第 21 行构造方法、第 50 行报错串前缀改 `"ModuleSetting.enabled:"`（D2，消息其余部分不动）、第 52 行 `findByType(ModuleSetting)`；消费方六行随改——`tny.module-setting.gradle` 第 3 行注释、第 6 行 `import tny.convention.ModuleSetting`、第 8 行 `extensions.create('moduleSetting', ModuleSetting, project)`（简名形态，经 import 随行）、`tny.module-checker.gradle` 第 33 行注释与第 45 行调用（含 `.Mode.UNPUBLISHED` 类型前缀随改、枚举名 `Mode` 不动）、`tny.integration-test.gradle` 第 117 行调用（含 `.Mode.APP` 前缀随类型名）。枚举 `Mode`、字段 `modes`、方法名与判定逻辑一律不动（D3）。验证：`./gradlew projects -q` 通过（覆盖配置期引用档；构造方法漏改属编译档当场即红；第 117 行执行期引用由 3.1 的 clean build 兜底）。

## 3. 零差异验收

- [x] 3.1 零差异核对：`grep -rn "ModuleModes" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github openspec/config.yaml` 现行面零命中（历史变更名 consolidate-module-modes-plugin 连字符形态本不属该判据，仍留档确认）；`grep -rn "ModuleModes\.enabled" .github tny-*/build.gradle` 零命中先证报错文案无自动化消费方（D2 风险条）；`tasks --all` 剔噪清单段对 1.1 基线逐行零差异；`./gradlew clean build` 全绿——如现 CollectionLockTest 同签名偶红按 fix-ci-unit-flakes 登记标准单任务 --rerun 复跑并留痕。全部结论记 verification-notes.md。
