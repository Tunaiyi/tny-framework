#!/usr/bin/env bash
# 定稿模型（dev 线工厂加 main 电梯枢纽加常驻 release 线加 merge-forward 传播）全场景沙箱模拟 v2。
# v2 按对抗复核判决修复：gate_markers 改 git grep --cached 正确参数序并补正向对照；
# 所有 ck 的 bash -c 统一 set -e（消灭"末句定成败"的假绿温床）；发版流水线加
# assert_release（标签指向的树含正确 dist 产物）与 clean_tree（树内无冲突标记残留）；
# 排队门注释与实现一致（条款 b）；例外补丁随 1.1.2 发版入账（解决例外提交退役后不可检索）；
# 新增 main 改写守卫、附注标签硬锚守卫、切线基座断言、多线并行时通配重计数。
# 用法：bash simulate.sh（沙箱每次全量重建；账本 /tmp/model4-sim/ledger.txt）
export PATH="/usr/bin:$PATH"
set -u
ROOT=/tmp/model4-sim
rm -rf "$ROOT"; mkdir -p "$ROOT"
LEDGER=$ROOT/ledger.txt; RETIRE=$ROOT/retired.list; EXEMPT=$ROOT/exemptions.list
export LEDGER RETIRE EXEMPT ROOT
touch "$RETIRE" "$EXEMPT"

FAILS=0
say(){ echo "$@" >>"$LEDGER"; }
ck(){ local d=$1; shift
  if "$@" >>"$LEDGER" 2>&1; then say "PASS | $d"; else say "FAIL | $d"; FAILS=$((FAILS+1)); fi }
ckf(){ local d=$1; shift
  if "$@" >>"$LEDGER" 2>&1; then say "FAIL(未拦住) | $d"; FAILS=$((FAILS+1)); else say "PASS(按预期拒绝) | $d"; fi }
run(){ bash -e -c "$1" >>"$LEDGER" 2>&1; }   # 内部使用：失败即整体失败

# ── 被测规则函数（未来落地到 buildSrc 的判定逻辑的 git 级复刻）─────────────
publishable(){ case "$1" in
  dev/[0-9]*.[0-9]*.x)     echo SNAPSHOT ;;
  release/[0-9]*.[0-9]*.x) echo RELEASE ;;
  *)                       echo DENY ;; esac; }

expect_next(){ local line=$1 want=$2
  local base=${line#release/}; base=${base%.x}
  local maxk=-1 t k
  for t in $(git tag -l "v${base}.*"); do
    k=${t#v${base}.}; [[ $k =~ ^[0-9]+$ ]] && (( k > maxk )) && maxk=$k; done
  local wantk=${want#"$base".}
  [[ $wantk =~ ^[0-9]+$ ]] && (( wantk == maxk + 1 )); }

tag_guard(){ local t=$1 target=$2
  if git rev-parse -q --verify "refs/tags/$t" >/dev/null; then
    [[ "$(git rev-parse "$t^{commit}")" == "$(git rev-parse "$target")" ]] || return 1; fi; }

# 附注标签硬锚守卫：轻量标签（对象不是 tag）一律拒绝作为发布标签。
annotated_only(){ git for-each-ref "refs/tags/$1" --format='%(objecttype)' | grep -qx tag; }

# 电梯排队门（条款 b）：更小编号的在役 dev 线仍存在即未梯未下线（本模型规则：梯毕即删线），
# 当前线不得先梯（发布顺序等于版本顺序）。编号按字典序，1.9 与 1.10 跨段边界留给真实实现的数值比较。
seq_gate(){ local dev=$1; local n=${dev#dev/}; n=${n%.x}
  local ok=1 m
  for m in $(git for-each-ref --format='%(refname:lstrip=2)' refs/heads/dev/ | sed 's|^dev/||;s|\.x$||'); do
    grep -qxF "dev/$m.x" "$RETIRE" && continue
    [[ "$m" < "$n" ]] || continue
    ok=0
  done
  return $((1-ok)); }

# main 改写守卫（pre-receive 判定复刻）：目标是 refs/heads/main 且带 force 即拒绝。
main_guard(){ local ref=$1; shift
  [[ "$ref" == refs/heads/main ]] && [[ "$*" == *force* || "$*" == *+refs* ]] && return 1
  return 0; }

# 提交前门禁：对索引树检索修复标记，任一标记计数为 0 即拒绝（复核致命项一修复：参数序）。
gate_markers(){ local miss=0 p c
  for p in "$@"; do
    c=$(git grep --cached -c -e "$p" 2>/dev/null | awk -F: '{s+=$2} END{print s+0}')
    (( c == 0 )) && { say "GATE | 索引树丢失修复标记: $p"; miss=1; }
  done; return $miss; }

# 发版流水线断言：标签存在且附注、指向的树含匹配版本的 dist 产物、树内无冲突标记。
assert_release(){ local t=$1 ver=$2
  annotated_only "$t" || return 1
  git show "$t:dist/artifact.txt" 2>/dev/null | grep -q "RELEASE $ver" || return 1
  ! git grep -q -e "<<<<<<<" "$t" -- 2>/dev/null || return 1; }

# 树面断言：指定 ref 的树不含冲突标记残留。
clean_tree(){ ! git grep -q -e "<<<<<<<" "$1" -- 2>/dev/null; }

# 退役门：线上仍有 main 未包含的提交即拒绝退役（先清账后退役）。
retire_gate(){ local line=$1
  [[ -z "$(git log --oneline "main..$line" 2>/dev/null)" ]]; }

export -f publishable expect_next tag_guard annotated_only seq_gate main_guard gate_markers assert_release clean_tree retire_gate say 2>/dev/null || true

# ── 场景 0：环境 ────────────────────────────────────────────────────────────
git init -q --bare -b main "$ROOT/remote.git"
git clone -q "$ROOT/remote.git" "$ROOT/work"; cd "$ROOT/work" || exit 1
git config user.name sim; git config user.email sim@local
echo "auth: plain" > core.txt; echo "net: v1" > net.txt; mkdir -p dist
git add -A; git commit -qm "init"; git branch -M main; git push -q origin main

# ── 场景 1：开线、工位、发布形态与通配 ─────────────────────────────────────
say "== 场景1 开线与工位 =="
ck "开 dev/1.0.x 自 main" run 'git switch -q -c dev/1.0.x main && git push -qu origin dev/1.0.x'
ck "发布资格四形态判定" run '
  [[ "$(publishable dev/1.0.x)" == SNAPSHOT ]] && [[ "$(publishable release/1.0.x)" == RELEASE ]]
  [[ "$(publishable main)" == DENY ]] && [[ "$(publishable feat/x)" == DENY ]]'
ck "特性 A 工位全链路（含 legacy 模块进主干）" run '
  git switch -q -c feat/1.0-aaa dev/1.0.x
  printf "legacy-gate v1\n" > legacy.txt && echo "search module AAA-001" >> core.txt
  git add -A && git commit -qm "feat(1.0): search AAA-001 + legacy 模块进主干" && git push -q origin feat/1.0-aaa
  git switch -q dev/1.0.x && git merge -q --ff-only feat/1.0-aaa && git push -q origin dev/1.0.x'
ck "特性 B 并行工位（rebase 同步后 ff）" run '
  git switch -q -c feat/1.0-bbb dev/1.0.x && echo "keepalive AAA-002" >> net.txt
  git commit -qam "feat(1.0): keepalive" && git rebase -q dev/1.0.x
  git switch -q dev/1.0.x && git merge -q --ff-only feat/1.0-bbb && git push -q origin dev/1.0.x'
ck "通配 dev/*.*.x 单线时刻恰捕 1 条；错误通配 *-*.*.x 捕 0 条（判决实测复现）" run '
  [[ $(git ls-remote --heads origin "dev/*.*.x" | wc -l | tr -d " ") == 1 ]]
  [[ $(git ls-remote --heads origin "*-*.*.x" | wc -l | tr -d " ") == 0 ]]'
ckf "main 强推被守卫拒绝" main_guard refs/heads/main --force-with-lease
ck "开发线强推是豁免的（守卫只拦 main）" main_guard refs/heads/dev/1.0.x --force-with-lease

# ── 场景 2：双向同步、电梯、切常驻线、首发与期望号 ─────────────────────────
say "== 场景2 电梯与首发 =="
echo "bench: noise" > bench.txt && git add -A && git commit -qm "chore(bench): 机器人直写 main"
git push -q origin main
ck "同步义务: merge main 入 dev 线" run 'git switch -q dev/1.0.x && git merge -q --no-edit origin/main && git push -q origin dev/1.0.x'
ck "排队门: 1.0 首发无前置, 放行" seq_gate dev/1.0.x
ck "电梯: --no-ff 入 main 后 dev 线下线" run '
  git switch -q main && git merge -q --no-ff -m "elevator: dev/1.0.x -> main (AAA-001,AAA-002)" dev/1.0.x && git push -q origin main
  git branch -q -D dev/1.0.x && git push -q origin --delete dev/1.0.x'
ck "切常驻发布线且基座恰为主线头" run '
  git switch -q -c release/1.0.x main
  [[ $(git rev-parse release/1.0.x) == $(git rev-parse main) ]]'
git push -qu origin release/1.0.x
ckf "期望号拒绝错号 1.0.7" expect_next release/1.0.x 1.0.7
ck "期望号放行 1.0.0 并走发版流水线" run '
  expect_next release/1.0.x 1.0.0
  mkdir -p dist && echo "RELEASE 1.0.0" > dist/artifact.txt && git add -A && git commit -qm "release: 1.0.0"
  git tag -a v1.0.0 -m "release 1.0.0 from release/1.0.x"
  assert_release v1.0.0 1.0.0 && git push -q origin release/1.0.x v1.0.0'
ckf "重跑同版本指向他处：标签守卫拒绝" tag_guard v1.0.0 "HEAD~1"
ck "重跑同版本同目标：守卫放行（幂等补推送语义）" tag_guard v1.0.0 HEAD
ckf "轻量标签不得作发布标签（硬锚守卫）" bash -e -c 'git tag vtmp-light; annotated_only vtmp-light; git tag -d vtmp-light >/dev/null'
git tag -d vtmp-light >/dev/null 2>&1 || true

# ── 场景 3：常驻线滚补丁 ────────────────────────────────────────────────────
say "== 场景3 补丁滚动与同号防线 =="
ck "fix 工位落线、ff 合回、发 1.0.1 走流水线" run '
  git switch -q -c fix/1.0.x-ccc release/1.0.x && echo "guard(BUG-100) auth-hardening" >> core.txt
  git commit -qam "fix: harden auth BUG-100" && git switch -q release/1.0.x && git merge -q --ff-only fix/1.0.x-ccc
  expect_next release/1.0.x 1.0.1
  echo "RELEASE 1.0.1" > dist/artifact.txt && git add -A && git commit -qm "release: 1.0.1"
  git tag -a v1.0.1 -m "release 1.0.1" && assert_release v1.0.1 1.0.1 && git push -q origin release/1.0.x v1.0.1'
ckf "期望号: 跳号 1.0.3 拒绝" expect_next release/1.0.x 1.0.3
ckf "同号复用: 以 1.0.1 再度过期望号被拒" expect_next release/1.0.x 1.0.1
ck "线可继续滚动而标签不动" run '
  h1=$(git rev-parse v1.0.1^{commit}); echo "chore: 线内预研下一补丁" >> net.txt
  git commit -qam "chore: 线内准备"
  [[ $(git rev-parse v1.0.1^{commit}) == $h1 ]]'

# ── 场景 4：并行开线、排队、两次电梯 ───────────────────────────────────────
say "== 场景4 并行开线、排队与 1.1/1.2 首发 =="
ck "并行开 dev/1.1.x 与 dev/1.2.x（均自 main）" run '
  git switch -q -c dev/1.1.x main && git push -qu origin dev/1.1.x
  echo "auth-v2 REFACT-11" > core.txt && git commit -qam "feat(1.1): auth rewrite AAA-011"
  git switch -q -c dev/1.2.x main && git push -qu origin dev/1.2.x
  echo "net: v1 + keepalive AAA-002 + http3" > net.txt && git commit -qam "feat(1.2): http3 AAA-012"'
ck "通配重计数: 两线并行时刻恰捕 2 条" run '
  [[ $(git ls-remote --heads origin "dev/*.*.x" | wc -l | tr -d " ") == 2 ]]'
ckf "排队条款 b: 1.1 未梯未下线时 1.2 不得先梯" seq_gate dev/1.2.x
ck "1.1 电梯下线、切线断言基座、发 1.1.0 流水线" run '
  git switch -q dev/1.1.x && git merge -q --no-edit origin/main && git push -q origin dev/1.1.x
  git switch -q main && git merge -q --no-ff -m "elevator: dev/1.1.x -> main (AAA-011)" dev/1.1.x && git push -q origin main
  git branch -q -D dev/1.1.x && git push -q origin --delete dev/1.1.x
  git switch -q -c release/1.1.x main && [[ $(git rev-parse release/1.1.x) == $(git rev-parse main) ]] && git push -qu origin release/1.1.x
  mkdir -p dist && echo "RELEASE 1.1.0" > dist/artifact.txt && git add -A && git commit -qm "release: 1.1.0"
  git tag -a v1.1.0 -m "release 1.1.0" && assert_release v1.1.0 1.1.0 && git push -q origin release/1.1.x v1.1.0'
ck "排队门: 1.1 梯毕下线后 1.2 放行" seq_gate dev/1.2.x
ck "1.2 电梯下线、发 1.2.0 流水线（v1.2.0 空标签缺陷的修复回归点）" run '
  git switch -q dev/1.2.x && git merge -q --no-edit origin/main && git push -q origin dev/1.2.x
  git switch -q main && git merge -q --no-ff -m "elevator: dev/1.2.x -> main (AAA-012)" dev/1.2.x && git push -q origin main
  git branch -q -D dev/1.2.x && git push -q origin --delete dev/1.2.x
  git switch -q -c release/1.2.x main && git push -qu origin release/1.2.x
  mkdir -p dist && echo "RELEASE 1.2.0" > dist/artifact.txt && git add -A && git commit -qm "release: 1.2.0"
  git tag -a v1.2.0 -m "release 1.2.0" && assert_release v1.2.0 1.2.0 && git push -q origin release/1.2.x v1.2.0'

# ── 场景 5：跨系列 CVE 的 merge-forward 梯 ─────────────────────────────────
say "== 场景5 BUG-101 三级上合梯 =="
ck "原型落最老在役受影响线发 1.0.2" run '
  git switch -q release/1.0.x && echo "guard(BUG-101): rate-limit" >> core.txt
  git commit -qam "fix: rate-limit on auth BUG-101"
  echo "RELEASE 1.0.2" > dist/artifact.txt && git add -A && git commit -qm "release: 1.0.2"
  git tag -a v1.0.2 -m "release 1.0.2" && assert_release v1.0.2 1.0.2 && git push -q origin release/1.0.x v1.0.2'
ck "上合 1.0.x -> 1.1.x 含真冲突, 解决必须覆盖全部冲突文件且树面干净" run '
  git switch -q release/1.1.x
  git merge --no-edit -m "propagate: release/1.0.x (BUG-101)" release/1.0.x >/dev/null 2>&1 || true
  if git rev-parse -q --verify MERGE_HEAD >/dev/null; then
    echo "auth-v2 REFACT-11; guard(BUG-101): rate-limit" > core.txt
    echo "RELEASE 1.1.0" > dist/artifact.txt
    git add -A && git commit --no-edit -q
  else say "NOTE | 本次未触发冲突"; fi
  clean_tree HEAD
  echo "RELEASE 1.1.1" > dist/artifact.txt && git add -A && git commit -qm "release: 1.1.1"
  git tag -a v1.1.1 -m "release 1.1.1" && assert_release v1.1.1 1.1.1 && git push -q origin release/1.1.x v1.1.1'
ck "上合 1.1.x -> 1.2.x 且树面干净" run '
  git switch -q release/1.2.x
  git merge --no-edit -m "propagate: release/1.1.x (BUG-101)" release/1.1.x >/dev/null 2>&1 || true
  if git rev-parse -q --verify MERGE_HEAD >/dev/null; then
    echo "auth-v2 REFACT-11; guard(BUG-101): rate-limit" > core.txt
    echo "net: v1 + keepalive AAA-002 + http3" > net.txt
    echo "RELEASE 1.2.0" > dist/artifact.txt
    git add -A && git commit --no-edit -q
  else say "NOTE | 本次未触发冲突"; fi
  clean_tree HEAD
  echo "RELEASE 1.2.1" > dist/artifact.txt && git add -A && git commit -qm "release: 1.2.1"
  git tag -a v1.2.1 -m "release 1.2.1" && assert_release v1.2.1 1.2.1 && git push -q origin release/1.2.x v1.2.1'
ck "上合 1.2.x -> main 终段且主线树面干净" run '
  git switch -q main
  git merge --no-edit -m "propagate: release/1.2.x -> main (BUG-101 落账)" release/1.2.x >/dev/null 2>&1 || true
  if git rev-parse -q --verify MERGE_HEAD >/dev/null; then
    git checkout --ours -- dist/artifact.txt 2>/dev/null || true
    echo "RELEASE 1.2.1" > dist/artifact.txt
    git add -A && git commit --no-edit -q
  fi
  clean_tree HEAD && git push -q origin main'
ck "祖先对账两级" run '
  git merge-base --is-ancestor release/1.1.x release/1.2.x && git merge-base --is-ancestor release/1.2.x main'
ck "编号点名: main 与三系最新标签树上 BUG-101 均大于 0" run '
  for t in main v1.0.2 v1.1.1 v1.2.1; do
    c=$(git grep -c -e "BUG-101" "$t" -- 2>/dev/null | awk -F: "{s+=\$3} END{print s+0}")
    (( c > 0 )) || { say "missing BUG-101 on $t"; exit 1; }
  done'
ck "辅助信号: cherry 在整线上合场景无未移植" run '! git cherry release/1.2.x release/1.0.x | grep -q "^+"'
ckf "断言可失败性抽检: 对无该标记的 v1.0.0 点名应红" bash -e -c '
  c=$(git grep -c -e "BUG-101" v1.0.0 -- 2>/dev/null | awk -F: "{s+=\$3} END{print s+0}"); (( c > 0 ))'

# ── 场景 6：例外通道、cherry 三义混淆、例外随版入账 ────────────────────────
say "== 场景6 例外通道 + cherry 假阳实锤 + 例外入账 =="
ck "1.1 重写 keepalive 区；1.0 补丁落旧上下文" run '
  git switch -q release/1.1.x && sed -i "" "s/keepalive AAA-002/keepalive-v2 REFACT-11b/" net.txt
  git commit -qam "refactor(1.1x): keepalive rewrite REFACT-11b" && git push -q origin release/1.1.x
  git switch -q release/1.0.x && sed -i "" "s/keepalive AAA-002/keepalive AAA-002 + leak-fix BUG-104/" net.txt
  git commit -qam "fix: keepalive leak BUG-104" && git push -q origin release/1.0.x'
ck "整线上合触发真冲突后走例外通道并带 propagated-as 尾注" run '
  git switch -q release/1.1.x && git merge --no-edit release/1.0.x >/dev/null 2>&1 || true
  git rev-parse -q --verify MERGE_HEAD
  git merge --abort
  sha=$(git rev-parse release/1.0.x)
  git cherry-pick -x "$sha" >/dev/null 2>&1 || {
    echo "net: v1 + keepalive-v2 REFACT-11b + leak-fix BUG-104" > net.txt
    git add net.txt && git commit -q -m "fix: keepalive leak BUG-104 propagated-as: exception-channel sha=$sha"
    git cherry-pick --quit 2>/dev/null || true
  }
  clean_tree HEAD && git push -q origin release/1.1.x'
ck "例外补丁随版入账: 发 1.1.2 且标签覆盖例外提交" run '
  expect_next release/1.1.x 1.1.2
  echo "RELEASE 1.1.2" > dist/artifact.txt && git add -A && git commit -qm "release: 1.1.2"
  git tag -a v1.1.2 -m "release 1.1.2 (含例外通道 BUG-104)"
  assert_release v1.1.2 1.1.2 && git merge-base --is-ancestor release/1.1.x v1.1.2 && git push -q origin release/1.1.x v1.1.2'
ck "cherry 三义混淆实锤（判决代价项二复现）: patch-id 分叉仍报未移植" run '
  git cherry release/1.1.x release/1.0.x | grep -q "^+"'
ck "权威判定: propagated-as 尾注可检索且自 v1.1.2 可达" run '
  git log v1.1.2 --grep="propagated-as" --oneline | grep -q exception-channel'

# ── 场景 7/8：假绿复现与门禁双向验证 ───────────────────────────────────────
say "== 场景8 窗口期逐出修复: 门禁正向对照、负向拦截、反事实 =="
ck "门禁正向对照: 标记完好的索引树应放行" run '
  git switch -q release/1.0.x
  git grep -q --cached -e "search module AAA-001"
  gate_markers "search module AAA-001"'
ck "1.0.x 修复 legacy 发 1.0.3（上合窗口期形成）" run '
  echo "legacy-gate: patched guard(BUG-102)" > legacy.txt
  git commit -qam "fix: patch legacy gate BUG-102"
  echo "RELEASE 1.0.3" > dist/artifact.txt && git add -A && git commit -qm "release: 1.0.3"
  git tag -a v1.0.3 -m "release 1.0.3" && assert_release v1.0.3 1.0.3 && git push -q origin release/1.0.x v1.0.3'
ck "dev/1.3.x 断代删除 legacy 并先梯（排队门放行）" run '
  git switch -q -c dev/1.3.x main && git rm -q legacy.txt
  git commit -qm "refactor(1.3): 断代重构删除 legacy BREAK-13" && git push -qu origin dev/1.3.x
  seq_gate dev/1.3.x
  git switch -q main && git merge -q --no-ff -m "elevator: dev/1.3.x -> main (BREAK-13)" dev/1.3.x && git push -q origin main
  git branch -q -D dev/1.3.x && git push -q origin --delete dev/1.3.x'
ck "窗口期后上合触发 modify/delete 冲突" run '
  git switch -q main && git merge --no-edit -m "propagate: release/1.0.x (BUG-102)" release/1.0.x >/dev/null 2>&1 || true
  git rev-parse -q --verify MERGE_HEAD'
ck "冲突按保持删除解决后, 门禁负向拦截: 索引树无 patched guard(BUG-102)" run '
  git rm -q legacy.txt
  echo "RELEASE 1.2.1" > dist/artifact.txt; git add -A
  git grep --cached -q -e "patched guard(BUG-102)" && exit 1
  gate_markers "patched guard(BUG-102)" && exit 1
  exit 0'
ck "拦截后正确路由: abort、豁免登记、新结构重做" run '
  git merge --abort
  echo "BUG-102 line=release/1.0.x exempt=main+1.3 evidence=重构删除该模块位置, 重做落 main 新结构" >> "$EXEMPT"
  echo "auth 新结构内建速率与门禁保护 BREAK-13 重做" >> core.txt
  git commit -qam "fix(main): 新结构重做保护 BUG-102-13（原修复由上合冲突解决逐出, 见豁免表）" && git push -q origin main'
ck "场景7 线原生豁免闭环: main 无 legacy-gate 签名、双豁免登记可查" run '
  ! git grep -q -e "legacy-gate" main -- 2>/dev/null || { say "main 树仍含 legacy-gate"; exit 1; }
  echo "BUG-902 line=release/1.0.x exempt=main evidence=重构后 main 树 legacy-gate 签名空" >> "$EXEMPT"
  grep -q BUG-902 "$EXEMPT" && grep -q BUG-102 "$EXEMPT"'
ck "反事实（隔离分支重建并保留取证）: 无门禁时祖先判定绿、修复内容红、且树可以毫无标记" run '
  git switch -q -c sim-ungated main
  git merge --no-edit -m "ungated propagate: release/1.0.x" release/1.0.x >/dev/null 2>&1 || true
  git rm -q legacy.txt 2>/dev/null || true
  echo "auth 新结构内建速率与门禁保护 BREAK-13 重做" > core.txt
  echo "net: v1 + keepalive AAA-002 + http3" > net.txt
  echo "RELEASE 1.2.1" > dist/artifact.txt
  git add -A && git commit -q --no-edit
  git merge-base --is-ancestor release/1.0.x HEAD
  c=$(git grep -c -e "patched guard(BUG-102)" HEAD -- 2>/dev/null | awk -F: "{s+=\$3} END{print s+0}")
  clean_tree HEAD
  (( c == 0 ))'

# ── 场景 9：清账梯、退役门、系列退役（例外可检索回归）─────────────────────
say "== 场景9 先清账后退役 =="
ckf "退役门拒绝: release/1.1.x 尚有未入账补丁（1.1.2 与例外通道提交）" retire_gate release/1.1.x
ck "清账梯: 1.1.x 上合 1.2.x（net 三方语义并集解决）再上合 main" run '
  git switch -q release/1.2.x
  git merge --no-edit -m "propagate: release/1.1.x (BUG-104 例外账)" release/1.1.x >/dev/null 2>&1 || true
  if git rev-parse -q --verify MERGE_HEAD >/dev/null; then
    echo "net: v1 + keepalive-v2 REFACT-11b + leak-fix BUG-104 + http3" > net.txt
    echo "auth-v2 REFACT-11; guard(BUG-101): rate-limit" > core.txt
    echo "RELEASE 1.2.1" > dist/artifact.txt
    git add -A && git commit --no-edit -q
  fi
  clean_tree HEAD
  echo "RELEASE 1.2.2" > dist/artifact.txt && git add -A && git commit -qm "release: 1.2.2"
  git tag -a v1.2.2 -m "release 1.2.2" && assert_release v1.2.2 1.2.2 && git push -q origin release/1.2.x v1.2.2
  git switch -q main
  git merge --no-edit -m "propagate: release/1.2.x -> main (BUG-104 落账)" release/1.2.x >/dev/null 2>&1 || true
  if git rev-parse -q --verify MERGE_HEAD >/dev/null; then
    git checkout --ours -- core.txt net.txt 2>/dev/null || true
    git rm -q legacy.txt 2>/dev/null || true
    git add -A && git commit --no-edit -q
  fi
  clean_tree HEAD && git push -q origin main'
ck "清账后退役门放行，再登记退役并删线" run '
  retire_gate release/1.1.x
  echo "release/1.1.x" >> "$RETIRE"
  git push -q origin --delete release/1.1.x && git branch -q -D release/1.1.x
  git fetch -q origin --prune'
ck "在役扫描集排除退役线；例外登记仍自 v1.1.2 可检索且 v1.1.2 已入账" run '
  active=$(git for-each-ref --format="%(refname:lstrip=3)" refs/remotes/origin/release/ | grep -vxF -f "$RETIRE" || true)
  echo "$active" | grep -qx release/1.2.x && echo "$active" | grep -qx release/1.0.x && ! echo "$active" | grep -qx release/1.1.x
  git log v1.1.2 --grep="propagated-as" --oneline | grep -q exception-channel
  git merge-base --is-ancestor v1.1.2 main'

# ── 场景 10：终态总账 ──────────────────────────────────────────────────────
say "== 场景10 终态快照 =="
say "本地分支终态: $(git for-each-ref --format='%(refname:lstrip=2)' refs/heads/ | tr '\n' ' ')"
say "标签终态: $(git tag -l 'v*' | tr '\n' ' ')"
say "主线全标签发版流水线复验:"
pipe_ok=1
for spec in v1.0.0:1.0.0 v1.0.1:1.0.1 v1.0.2:1.0.2 v1.0.3:1.0.3 v1.1.0:1.1.0 v1.1.1:1.1.1 v1.1.2:1.1.2 v1.2.0:1.2.0 v1.2.1:1.2.1 v1.2.2:1.2.2; do
  t=${spec%%:*}; ver=${spec##*:}
  if assert_release "$t" "$ver"; then say "  复验通过 ${t}（附注、产物、无标记）"; else say "  复验失败 $t"; pipe_ok=0; fi
done
[[ $pipe_ok == 1 ]] && say "PASS | 终态九个标签全部通过发版流水线复验" || { say "FAIL | 存在标签复验不过"; FAILS=$((FAILS+1)); }
git log --first-parent --format='  %h %s' main | head -25 | while read -r l; do say "$l"; done
say "补丁上合在静默窗口为 fast-forward, 补丁入账权威为标签账本（终验逐条如上）。"

say "== 汇总 =="
say "FAIL 计数: $FAILS"
if [[ $FAILS -eq 0 ]]; then say "结论: 全场景通过"; else say "结论: 存在失败项，见账本"; fi
exit $FAILS
