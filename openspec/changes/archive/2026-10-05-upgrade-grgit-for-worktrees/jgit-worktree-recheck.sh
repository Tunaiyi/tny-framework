#!/bin/bash
# JGit linked-worktree 解析复验脚本（upgrade-grgit-for-worktrees watch 项）。
# 用法：bash jgit-worktree-recheck.sh [jgit-jar路径...]
# 不带参数时用 gradle 缓存的现版本 + 下载 grgit 5.3.2 锁定的 6.10.1 对照。
# 判读：resolveHEAD 两行均 null → 缺陷仍在，守卫保留；任一非 null → 记录版本，
# 评估升级解禁与 tny.git 守卫撤除（另立变更）。
set -e
export PATH="/usr/bin:$PATH"
REPO=/tmp/jgit-recheck-clone
WT=/tmp/jgit-recheck-wt
rm -rf "$REPO" "$WT" 2>/dev/null || true
git clone -q --no-checkout https://github.com/Tunaiyi/tny-framework.git "$REPO"
git -C "$REPO" worktree add -q --detach "$WT" HEAD
SLF=$(find ~/.gradle/caches/modules-2 -name "slf4j-api-*.jar" | sort | tail -1)
for JAR in "$@" \
  "$(find ~/.gradle/caches -name 'org.eclipse.jgit-5.13*.jar' ! -name '*sources*' | head -1)" \
  "/tmp/org.eclipse.jgit-6.10.1.jar"; do
  [ -f "$JAR" ] || { echo "skip missing jar: $JAR"; continue; }
  echo "--- $(basename "$JAR") ---"
  jshell --class-path "$JAR:$SLF" -s - <<EOF
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
var b = new FileRepositoryBuilder();
b.findGitDir(new File("$WT"));
var repo = new FileRepositoryBuilder().setGitDir(b.getGitDir()).build();
var h = repo.resolve("HEAD");
System.out.println("gitdir=" + b.getGitDir());
System.out.println("branch=" + repo.getBranch());
System.out.println("resolveHEAD=" + h);
/exit
EOF
done
git -C "$REPO" worktree remove --force "$WT" 2>/dev/null || true
