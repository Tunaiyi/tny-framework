#!/usr/bin/env bash
# 端到端演练（任务 8.2 的预演版）：在 /tmp 完整克隆里用真实 gradle 任务跑新模型生命周期——
# dev 开线、集成顺序检查、integrateMain、releaseCut 切维护分支、releaseTag（含下一补丁号校验）、
# mergeUpward（含冲突回滚与无门禁对照案）、祖父轨 releaseCut dryRun。每次全量重建。
# 用法：bash drill/model4-sim/e2e-gradle.sh   （需要 JDK21 与 git，可用环境变量覆盖）
set -u
export PATH="/usr/bin:$PATH"
export JAVA_HOME="${JAVA_HOME:-/Users/kgtny/Library/Java/JavaVirtualMachines/corretto-21.0.12.1/Contents/Home}"
SRC="$(cd "$(dirname "$0")/../.." && pwd)"
# 目录带进程号后缀：同路径重建会让 Gradle 守护进程按路径复用旧 buildSrc 类loader，
# 改过插件代码后同路径重跑会拿旧类执行（第七轮实锤）；换路径强制新配置。
ROOT="/tmp/model4-e2e-$$"
LEDGER="$ROOT/ledger.txt"
rm -rf "$ROOT"; mkdir -p "$ROOT"
# 关键隔离：克隆的 origin 指向真实仓库，演练的推送必须改打独立裸仓——
# 首版脚本直接把 release/dev 分支推进了工作库（已清理），教训记入 apply-notes。
# 二版教训：clone 后 set-url 改指向会残留旧远端的 remote-tracking 引用，配置期
# 读到陈旧头指针造成连环假失败；三版教训：在 shell 当前目录里 rm -rf 自己会让
# 后续 git 全部踩空。正解：先做裸镜像再从镜像克隆，全程单一干净 origin，删除动作
# 只发生在进入任何目录之前。
git clone -q --bare "$SRC" "$ROOT/mirror.git"
git clone -q "$ROOT/mirror.git" "$ROOT/work"
cd "$ROOT/work" || exit 1
git config user.name e2e; git config user.email e2e@local
: > "$LEDGER"
FAILS=0
say(){ echo "$@" | tee -a "$LEDGER"; }
expect(){ local d=$1 want=$2; shift 2
  if "$@" >>"$LEDGER" 2>&1; then r=ok; else r=fail; fi
  if [ "$r" = "$want" ]; then say "PASS($r 如预期) | $d"; else say "FAIL(期望$want 实$r) | $d"; FAILS=$((FAILS+1)); fi
}
G(){ ./gradlew "$@" -PgitExe=/usr/bin/git; }

# 场景准备：main 与 dev/5.8.x 就位并推送（main 以 5.7.x 为底重建并强推到镜像——
# 镜像非真实仓库，此处强推是演练环境搭建，不违反 main 只追加铁律）
git checkout -q -B main 5.7.x
echo "base marker" > e2e-base.txt; git add e2e-base.txt; git commit -qm "chore(e2e): base"
git push -q -f -u origin main
git checkout -q -b dev/5.8.x
echo "feat-58 module" >> e2e-base.txt; git commit -qam "feat(5.8): sample feature AAA-5800"
git push -q -u origin dev/5.8.x

# 1 集成顺序检查：更低的在途开发分支 dev/5.7.x 存在即阻塞
git checkout -q -b dev/5.7.x main; git push -q origin dev/5.7.x
git checkout -q dev/5.8.x
expect "顺序检查阻塞：dev/5.7.x 未集成未下线时 5.8 不得梯" fail G integrateMain -PreleaseVersion=5.8.0
git push -q origin --delete dev/5.7.x
git checkout -q main && git branch -q -D dev/5.7.x
git checkout -q dev/5.8.x

# 2 正式集成
expect "integrateMain 成功（同步、合并、下线）" ok G integrateMain -PreleaseVersion=5.8.0
say "main 头: $(git log main --first-parent -1 --format='%s')"
expect "dev/5.8.x 本地与远端均已删除" ok bash -c '! git rev-parse --verify dev/5.8.x && [ -z "$(git ls-remote --heads origin dev/5.8.x)" ]'

# 3 切常驻维护分支
expect "非 .0 版本在 main 切维护分支被拒" fail G releaseCut -PreleaseVersion=5.8.1
expect "releaseCut 5.8.0 切 release/5.8.x" ok G releaseCut -PreleaseVersion=5.8.0
expect "维护分支已在远端" ok bash -c 'git ls-remote --heads origin refs/heads/release/5.8.x | grep -q release/5.8.x'
expect "main 上重复切 5.8 被拒（系列唯一）" fail G releaseCut -PreleaseVersion=5.8.0
git fetch -q origin
# releaseCut 已把维护分支建在本地（git branch 自 origin/main），此处直接切换
git checkout -q release/5.8.x

# 4 首发打标签
expect "releaseTag 首发 5.8.0（下一补丁号期望 0）" ok G releaseTag -PreleaseVersion=5.8.0
expect "同号重复 releaseTag 被远端标签存在拒" fail G releaseTag -PreleaseVersion=5.8.0

# 5 补丁滚动
echo "guard(BUG-7001): rate limit" > bug7001.txt; git add bug7001.txt
git commit -qm "fix: rate limit BUG-7001"
git push -q origin release/5.8.x
expect "跳号 5.8.2 被下一补丁号校验拒" fail G releaseTag -PreleaseVersion=5.8.2
expect "releaseTag 5.8.1 成功" ok G releaseTag -PreleaseVersion=5.8.1

# 6 向上合并与两个对照案
git checkout -q main; echo "unrelated" > e2e-note.txt; git add e2e-note.txt; git commit -qm "chore: note"
git push -q origin main
git checkout -q release/5.8.x
expect "mergeUpward 到 main（显式标记核验通过）" ok G mergeUpward -Pmarkers=BUG-7001
expect "main 已包含 v5.8.1（祖先判定）" ok git merge-base --is-ancestor v5.8.1 main
# 冲突回滚案
git checkout -q main; git rm -q bug7001.txt; git commit -qm "refactor: 删除旧结构 BREAK-59（模拟）"; git push -q origin main
git checkout -q release/5.8.x; echo "guard(BUG-7001): rate limit v2" > bug7001.txt
git commit -qam "fix: BUG-7001 二次加固"
# 真实流程顺序：修复合入源分支即推送（九轮教训：releaseTag 要求本地与远端头对齐）
git push -q origin release/5.8.x
expect "releaseTag 5.8.2 成功" ok G releaseTag -PreleaseVersion=5.8.2
HEAD_BEFORE=$(git rev-parse main)
expect "mergeUpward 遇 modify/delete 冲突：自动回滚并拒绝" fail G mergeUpward -Pmarkers=BUG-7001
expect "main 未被污染（已回滚到原头）" ok bash -c "[ \"$(git rev-parse main)\" = \"$HEAD_BEFORE\" ]"
# 无门禁对照：人工"保持删除"合并 → 历史绿而树丢标记
git checkout -q main
git merge --no-edit release/5.8.x >/dev/null 2>&1 || true
git checkout --ours -- . 2>/dev/null || true
git rm -q bug7001.txt 2>/dev/null || true; git add -A
git commit -qm "ungated merge with loss（对照演示）" || git merge --quit
# 检索须排除叙述性文件（十一轮实锤：本演练脚本自含标记字样，不排除则自我蒙蔽恒绿）
C=$(git grep -c BUG-7001 HEAD -- ':(exclude)drill' ':(exclude)docs' ':(exclude)openspec' 2>/dev/null | awk -F: '{s+=$3} END{print s+0}')
if [ "${C:-0}" = "0" ] && git merge-base --is-ancestor release/5.8.x HEAD; then
  say "PASS(对照) | 无门禁合并确实造成历史绿而树丢标记——mergeUpward 内置检查防的正是此事"
else
  say "FAIL(对照) | 未复现丢失路径"; FAILS=$((FAILS+1))
fi
git reset -q --hard "$HEAD_BEFORE"; git checkout -q release/5.8.x

# 7 祖父轨 dryRun 判入旧流程（真实本地分支名 5.7.x 命中登记表）
git checkout -q 5.7.x
expect "祖父线 releaseCut dryRun 判入旧一次性流程" ok bash -c "cd '$ROOT/work' && ./gradlew releaseCut -PreleaseVersion=5.7.99 -PdryRun -PgitExe=/usr/bin/git 2>&1 | grep -q '祖父轨'"
git checkout -q release/5.8.x

say "FAIL 计数: $FAILS"
[ "$FAILS" -eq 0 ] && say "结论: 端到端演练全绿" || say "结论: 存在失败项见账本"
exit "$FAILS"
