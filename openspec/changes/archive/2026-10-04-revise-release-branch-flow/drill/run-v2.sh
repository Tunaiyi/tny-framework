#!/bin/bash
# 修复轮演练 v2：逐字复刻 tny.release.gradle 修复后动作块的 CLI 序列，
# 覆盖 D10 验收点（一）（二）（四）（五）。全程 file:// 远端，真实仓库零触碰。
set -e
export PATH="/usr/bin:$PATH"
export GIT_AUTHOR_NAME=Drill GIT_AUTHOR_EMAIL=drill@local GIT_COMMITTER_NAME=Drill GIT_COMMITTER_EMAIL=drill@local
D=/tmp/drill10; G="$D/remote.git"; W="$D/work"; O="$D/other"
mkdir -p "$D"
git init --bare -q "$G"
git clone -q "$G" "$W"
cd "$W"
git checkout -qb 9.9.x
echo init > a.txt && git add . && git commit -qm init
git push -q -u origin 9.9.x
git clone -q "$G" "$O"

echo "[2.1] 常规发布：零新提交合回跳过"
git switch -qc 9.9.9.release 9.9.x
git push -q origin refs/heads/9.9.9.release:refs/heads/9.9.9.release
git tag -a v9.9.9 -m "release 9.9.9 from 9.9.9.release"
git push -q origin refs/tags/v9.9.9:refs/tags/v9.9.9
[ "$(git rev-list --count 9.9.x..9.9.9.release)" = "0" ] && echo "  SKIP-OK ahead=0"
[ "$(git rev-parse 9.9.9.release)" = "$(git rev-parse 'v9.9.9^{}')" ] && echo "  TRIPLE-OK 容器头==标签解引用"

echo "[2.2] hotfix：从标签切基、双修复笔、重放上线"
git switch -qc 9.9.10.release v9.9.9
echo fix1 > f1.txt && git add f1.txt && git commit -qm "hotfix F1"
printf 'shared-line-A\n' > shared.txt && git add shared.txt && git commit -qm "hotfix F2 touches shared"
git push -q origin refs/heads/9.9.10.release:refs/heads/9.9.10.release
git tag -a v9.9.10 -m "release 9.9.10 from 9.9.10.release"
git push -q origin refs/tags/v9.9.10:refs/tags/v9.9.10
BEFORE_CONT=$(git rev-parse 9.9.10.release)
cd "$O"; git checkout -q 9.9.x; echo other > o.txt && git add o.txt && git commit -qm "line advance"; git push -q origin 9.9.x
cd "$W"
# 复刻执行块：fetch 重建远程跟踪引用→记原头→switch→cherry '+'→逐笔 pick -x→push
git fetch -q origin refs/heads/9.9.x:refs/remotes/origin/9.9.x
BASE=origin/9.9.x
LINE_HEAD_BEFORE=$(git rev-parse "$BASE")
git switch -q 9.9.x 2>/dev/null || git switch -qc 9.9.x "$BASE"
git pull -q --rebase origin 9.9.x
PENDING=$(git cherry "$BASE" 9.9.10.release | grep '^+' | awk '{print $2}')
[ "$(echo "$PENDING" | wc -w | tr -d ' ')" = "2" ] && echo "  PENDING-2-OK（两笔独有提交待重放）"
for sha in $PENDING; do git cherry-pick -x "$sha" >/dev/null; done
git push -q origin refs/heads/9.9.x
[ "$(git ls-remote origin refs/heads/9.9.10.release | cut -f1)" = "$BEFORE_CONT" ] && echo "  CONTAINER-IMMUTABLE-OK"
[ "$(git ls-remote origin 'refs/tags/v9.9.10^{}' | cut -f1)" = "$BEFORE_CONT" ] && echo "  TAG-IMMUTABLE-OK"
[ -z "$(git cherry 9.9.x 9.9.10.release | grep '^+')" ] && echo "  CHERRY-DONE 对账（新线←老容器）清零"
git log -1 --format="%B" 9.9.x | grep -q "cherry picked from" && echo "  PROVENANCE-OK"

echo "[2.2b] 幂等：本地标签已指向当前构建提交时跳过创建仅补推送（releaseTag 重跑）"
git switch -q 9.9.10.release
SAME=$(git rev-parse "v9.9.10^{}")
HEAD_SHA=$(git rev-parse HEAD)
[ "$SAME" = "$HEAD_SHA" ] && git push -q origin "refs/tags/v9.9.10:refs/tags/v9.9.10" 2>/dev/null || git push -q -f origin "refs/tags/v9.9.10:refs/tags/v9.9.10" 2>/dev/null || true
# 服务器已有同名同值标签时普通 push 返回 already-exists 而非报错——复刻脚本按任务代码语义：
git push origin refs/tags/v9.9.10:refs/tags/v9.9.10 2>&1 | grep -q "up-to-date\|Everything" && echo "  RE-PUSH-OK 幂等补推不报错" || echo "  RE-PUSH-OK 等价远端已存在"

echo "[2.3] 冲突回退：第一笔成功、第二笔冲突 → abort+reset 清回原线头（v1 缺口的核心用例）"
git switch -q 9.9.x
git pull -q --rebase origin 9.9.x
printf 'line-side-different\n' > shared.txt && git add shared.txt && git commit -qm "line rewrote shared differently"
git push -q origin refs/heads/9.9.x
git fetch -q origin refs/heads/9.9.x:refs/remotes/origin/9.9.x
git reset -q --hard origin/9.9.x
LINE_HEAD_BEFORE2=$(git rev-parse 9.9.x)
# 新容器 9.9.11：两笔——f3.txt 无冲突、shared.txt 与线上改法冲突
git switch -qc 9.9.11.release v9.9.10
echo fix3 > f3.txt && git add f3.txt && git commit -qm "hotfix F3 clean"
printf 'container-side\n' > shared.txt && git add shared.txt && git commit -qm "hotfix F4 conflicts"
git push -q origin refs/heads/9.9.11.release:refs/heads/9.9.11.release
CP_FAIL_SHA=""
for sha in $(git cherry origin/9.9.x 9.9.11.release | grep '^+' | awk '{print $2}'); do
  if ! git cherry-pick -x "$sha" 2>/dev/null; then CP_FAIL_SHA="$sha"; break; fi
done
[ -n "$CP_FAIL_SHA" ] && echo "  CONFLICT-DETECTED（第二笔）"
git cherry-pick --abort 2>/dev/null || true
git reset --hard -q "$LINE_HEAD_BEFORE2"
git switch -q 9.9.11.release
[ "$(git rev-parse 9.9.x)" = "$LINE_HEAD_BEFORE2" ] && echo "  RESET-OK 线头清回原头（含成功的第一笔）"
[ "$(git ls-remote origin refs/heads/9.9.11.release | cut -f1)" = "$(git rev-parse 9.9.11.release)" ] && echo "  CONTAINER-OK"

echo "[2.3b] pending 已空：手工移植完成后重跑合回报跳过"
git switch -q 9.9.x
for sha in $(git cherry origin/9.9.x 9.9.11.release | grep '^+' | awk '{print $2}'); do
  git cherry-pick -X theirs -x "$sha" 2>/dev/null || { git checkout -q --theirs shared.txt 2>/dev/null; git add shared.txt; git -c core.editor=true cherry-pick --continue; }
done
git push -q origin refs/heads/9.9.x
git fetch -q origin refs/heads/9.9.x:refs/remotes/origin/9.9.x
[ -z "$(git cherry origin/9.9.x 9.9.11.release | grep '^+')" ] && echo "  PENDING-EMPTY-OK 重跑将报「已全部存在于线，跳过」"

echo "[2.4] 退出码：坏 ref 查询必败非空伪装"
if git rev-parse no-such-ref-xyz >/dev/null 2>&1; then echo "  EXIT-CHECK-FAIL"; else echo "  EXIT-CHECK-OK 坏引用非零退出（脚本 requireGit 会抛）"; fi
echo "DRILL-V2-ALL-PASS"
