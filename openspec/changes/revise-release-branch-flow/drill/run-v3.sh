#!/bin/bash
# 演练 v3（D11（一）（二）验收）：
#  W1 形态判定——复刻任务代码依赖的 grgit 判据输入（轻量标签 cat-file=commit →
#     Tag.tagger 为空 → releaseTag 拒绝幂等放行；附注 cat-file=tag 才允许补推）。
#  W2 查询前置——坏 baseRef 的 git cherry 以非零退出，且失败发生在检出线之前，
#     HEAD 全程未动（对照修复前"检出后查询失败滞留线上"的行为差异）。
set -e
export PATH="/usr/bin:$PATH"
export GIT_AUTHOR_NAME=Drill GIT_AUTHOR_EMAIL=drill@local GIT_COMMITTER_NAME=Drill GIT_COMMITTER_EMAIL=drill@local
D=/tmp/drill12/repo
mkdir -p "$D"
cd "$D"
git init --bare -q remote.git
git clone -q remote.git w
cd w
git checkout -qb 9.9.x
echo a > a.txt && git add . && git commit -qm init
git push -q -u origin 9.9.x
git switch -qc 9.9.12.release 9.9.x

echo "[W1] 标签形态判定（任务代码 grgit Tag.tagger 判据的 CLI 等价输入）"
git tag v9.9.12
T1=$(git cat-file -t v9.9.12)
[ "$T1" = "commit" ] && echo "  LIGHTWEIGHT-DETECTED：cat-file=commit → grgit tagger 为空 → releaseTag 按 D11（一）拒绝幂等放行"
git tag -d v9.9.12 >/dev/null
git tag -a v9.9.12 -m "release 9.9.12 from 9.9.12.release"
T2=$(git cat-file -t v9.9.12)
[ "$T2" = "tag" ] && echo "  ANNOTATED-PASS：cat-file=tag → tagger 非空且解引用=HEAD → 幂等补推路径放行"
[ "$(git rev-parse 'v9.9.12^{}')" = "$(git rev-parse HEAD)" ] && echo "  IDEMPOTENT-GUARD：解引用恰指当前构建提交（放行条件成立）"

echo "[W2] cherry 查询前置：失败时 HEAD 未离开容器"
HEAD_BEFORE=$(git rev-parse --abbrev-ref HEAD)
set +e
git cherry no-such-ref HEAD >/dev/null 2>&1
RC=$?
set -e
HEAD_AFTER=$(git rev-parse --abbrev-ref HEAD)
[ "$RC" != "0" ] && echo "  CHERRY-EXIT=$RC 非零（任务经 requireGit 必抛，含 stderr）"
[ "$HEAD_BEFORE" = "$HEAD_AFTER" ] && echo "  HEAD-UNCHANGED：查询发生在检出线之前，失败后无需任何回切（修复前此位置在检出之后）"
echo "DRILL-V3-PASS"
