---
name: mcp-env-init
description: 初始化/重建/排障本机的 MCP 环境（GitHub MCP、Docker MCP、Docker skills 插件、docker mcp gateway）。当用户说「初始化 MCP」「重建 MCP 环境」「MCP 连不上」「换机器重装」或需要引用本机 MCP 接线方式时使用。
---

# MCP 环境初始化（本机实测配方）

2026-10-01 在本机（macOS arm64 / OrbStack / 无 gh CLI / git clone 受网络限制）完整验证过的搭建与排障流程；2026-10-04 将 GitHub MCP 接线改为 GitHub 托管远程端点 + PAT 并冒烟通过（§1）。
所有步骤按序执行；带 ⚠ 的是本机特有约束，别按通用文档照抄。

## 架构总览（谁在跑）

| 组件 | 形态 | 宿主入口 |
|---|---|---|
| GitHub MCP | GitHub 托管远程端点（`https://api.githubcopilot.com/mcp/`，静态 PAT header，无本地容器） | `claude mcp get github` |
| docker-mcp (L337-org 2.2.6) | stdio 容器 + docker.sock 挂载，165 工具 | `claude mcp get docker` |
| docker-skills 插件 v0.3.1 | Claude Code plugin（user scope） | `claude plugin list` |
| docker mcp gateway v0.44.1 | 宿主 CLI 插件进程（stdio，profile tny，当前挂 docker-docs remote） | `claude mcp get docker-gateway` |
| 项目 compose 栈 | `docker/docker-compose.yaml`（etcd/redis/mongo, project=tny-framework-dev）；`docker/docker-compose.mcp.yaml`（MCP stdio 单服务声明：2026-10-04 起仅存 mcp-docker，宿主 compose run 消费） | 见仓库根 |

**关键原则**：Claude Code 会话启动后才 `claude mcp add` 的 server，本会话不加载，需重启会话验证。

## 1. GitHub MCP — GitHub 托管远程端点 + PAT（现网接线，2026-10-04 切换）

现网形态（user scope，注册后配置落在 `~/.claude.json` 顶层 `mcpServers` 的 github 条目里，token 随配置文件持久保存，重启电脑或会话后不再发生任何授权）：

```bash
claude mcp add github -s user -t http https://api.githubcopilot.com/mcp/ \
  -H "Authorization: Bearer <classic PAT>"
```

- PAT 在 https://github.com/settings/tokens/new 创建（classic 型），scope 勾选 `repo read:org user notifications read:packages write:packages`，与旧接线 OAuth 授予的权限一一对应。
- ⚠ header 里不得写 `${VAR}` 占位符：Claude Code 会刻意把远程服务器 `url`/`headers` 中的凭据类变量名读成空值（官方文档规则，查证于 2026-10-04），服务端收到的 `Bearer ` 不带凭据必然 401。要么写字面 token，要么改用 `headersHelper` 在连接时动态生成 header。
- ⚠ 远程端点的 OAuth 路径仍然不通：GitHub OAuth 服务器不支持 DCR（动态客户端注册），Claude Code 拿不到 client_id，会报 `Incompatible auth server: does not support dynamic client registration`。静态 PAT header 是唯一绕过授权的途径。
- 冒烟测试（不必等会话重启）：用 curl 向端点发一条带 bearer header 的 MCP initialize POST 请求，返回 200 与 serverInfo 正文即证明 token、网络、header 三者都通（2026-10-04 实测通过）。
- 会话内验收：直接调 `get_me` 成功，且不应再出现任何授权 URL。
- 历史：2026-10-04 之前的接线是 stdio 容器 `mcp-github-stdio`（compose 声明式 + 浏览器 OAuth + localhost:8085 回调），token 仅存内存，每次重启都要重新授权。该服务的定义与那条接线的实测坑（固定容器名单实例、回调端口必须宿主容器一致、server 端 pending flow 单例）见本文件与 `docker/docker-compose.mcp.yaml` 的 git 历史。8085 端口已随接线退役整体释放。

## 2. Docker MCP — stdio 直连 + socket 挂载

现网形态（compose 声明式，来源 = `docker/docker-compose.mcp.yaml` 的 `mcp-docker` 服务）：

```bash
claude mcp add docker -s user -- \
  docker compose -f <repo>/docker/docker-compose.mcp.yaml --profile stdio run --rm -T --name tny-mcp-docker mcp-docker
```
固定名 `tny-mcp-docker` 便于 docker exec/logs 定位；**代价是单实例**——多会话并行或 `claude mcp list` 健康探测二次 spawn 会撞名。stdio 管道由宿主 spawn 持有，`up` 不拉 stdio 服务（profile 隔离的缘由）。

- `/var/run/docker.sock` 由 OrbStack **VM 侧**解析（容器与 daemon 同 VM），Mac 上没有该路径也正常工作；
  compose 挂载源写 `~/.orbstack/run/docker.sock` 反而不通（virtiofs 不支持跨 OS unix socket）。
- 镜像自带 docker CLI + Compose v2，compose 域真实可用；但容器看不到 Mac 文件系统——
  compose_* 的 project_dir 参数指**容器内路径**，操作仓库文件需先注入（`container_exec` heredoc）或走 Mac 宿主 CLI。
- 验证：`claude mcp get docker` → Connected；会话内 `container_list` 返回实容器即端到端通。

## 3. Docker Agent Skills 插件（官方 docker/skills）

⚠ 本机 git clone 不通（代理只放行 HTTPS 下载），`claude plugin marketplace add docker/skills` 会失败。改走 curl 取 tar 注册**本地 marketplace**：

```bash
mkdir -p /tmp/dks && curl -fsSL https://codeload.github.com/docker/skills/tar.gz/refs/heads/main -o /tmp/dks/s.tgz \
  && tar xzf /tmp/dks/s.tgz -C /tmp/dks
claude plugin marketplace add /tmp/dks/skills-main
claude plugin install docker-skills@docker
```

## 4. Docker MCP Gateway（可选，官方目录托管器）

```bash
# 4.1 装 CLI 插件（release 含 darwin-arm64 预编译）
mkdir -p ~/.docker/cli-plugins && curl -fsSL -o /tmp/gw.tar.gz \
  https://github.com/docker/mcp-gateway/releases/download/v0.44.1/docker-mcp-darwin-arm64.tar.gz \
  && tar xzf /tmp/gw.tar.gz -C ~/.docker/cli-plugins && chmod +x ~/.docker/cli-plugins/docker-mcp
# 4.2 独立模式（非 Desktop）两个必带 env：
#     DOCKER_MCP_IN_CONTAINER=1           绕过 Desktop 特性检查
#     DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock   gateway 不读 docker context
export DOCKER_MCP_IN_CONTAINER=1 DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock
docker mcp feature enable profiles
# 4.3 目录与 profile（官方目录 mcp/docker-mcp-catalog:latest 为 vetted；community 未审）
docker mcp catalog pull mcp/docker-mcp-catalog:latest
docker mcp profile create tny --server catalog://mcp/docker-mcp-catalog:latest/docker-docs
# 4.4 接入 Claude
claude mcp add docker-gateway -s user -e DOCKER_MCP_IN_CONTAINER=1 \
  -e DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock -- docker mcp gateway run --profile tny
```

- ⚠ gateway 硬编码禁挂敏感路径（`/var/run`、`/dev`、`/etc`…，源码 `pkg/gateway/docker_binds.go`，无豁免 env）→ **docker.sock 永远进不了 gateway server**；容器管理类需求走第 2 节直连方案。
- ⚠ `file://` 引用是 catalog 格式（顶层 `registry:`，条目 `type: server`，`volumes: ["h:c:rw"]`）；`docker://` 镜像引用要求镜像自描述（OCI 注解）。
- ⚠ 无 Desktop 时 secrets 引擎缺失（`docker-secrets-engine` socket 不存在）→ 只需 env 的 server 不受影响；要 secret 的 server 用 `--secrets` 指本地 .env 兜底。

## 5. 项目 compose 栈速查

```bash
docker compose -f docker/docker-compose.yaml up -d                 # etcd:2379 + redis:6379（+ mongo）
MONGO_HOST_PORT=27018 docker compose -f docker/docker-compose.yaml up -d mongo   # 27017 被 x35 项目占用时的用法
# docker/docker-compose.mcp.yaml 无 up 型服务：stdio 单服务（mcp-docker）由宿主 compose run 按需拉起（见 §2）
```
详见两文件头注释（参数均对齐 IT 代码与 CI，勿随意改动 command/端口）。

## 6. 全链路验证清单

```bash
claude mcp list                          # 全绿 Connected
# stdio 握手探针（现仅 docker server 需要；github 已改远程端点，冒烟方法见 §1）：
( printf '%s\n' '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"p","version":"0"}}}' '{"jsonrpc":"2.0","method":"notifications/initialized"}' '{"jsonrpc":"2.0","id":2,"method":"tools/list"}'; sleep 6 ) \
  | docker run -i --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/l337-org/docker-mcp-server:2.2.6 2>/dev/null | tail -1 | head -c 200
nc -z localhost 2379 && nc -z localhost 6379          # 项目 compose 栈
./gradlew :tny-game-namnspace-etcd:test -q            # 2379 单测依赖路径
```

## 7. 故障对照表

| 症状 | 根因 | 处置 |
|---|---|---|
| `Incompatible auth server: does not support DCR` | 尝试走 OAuth 流程（GitHub 无 DCR） | 走 §1 的静态 PAT header 接线；OAuth 路线在本机没有可行路径 |
| gateway 报 `Docker Desktop is not running` | Desktop 特性检查 | `DOCKER_MCP_IN_CONTAINER=1` |
| gateway 报 `Cannot connect to the Docker daemon at unix:///var/run/docker.sock` | 不读 docker context | `DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock` |
| gateway 报 `host path "/var/run/docker.sock" is blocked (sensitive system path)` | 安全策略硬禁 | 容器管理类改用 §2 直连 |
| `secrets engine is not available` | 非 Desktop 无 secrets store | 仅影响需 secret 的 server；env 兜底 |
| `claude plugin marketplace add <owner>/<repo>` 克隆失败 | 本机 git 通道受限 | curl tar → 本地路径注册（§3） |
| MCP 配置成功但会话里没有工具 | 会话早于配置启动 | 重启 `claude` 或 `--continue` |
| GitHub 调用 401 | PAT 过期或被吊销、scope 不足，或 header 写了凭据占位变量被读成空值（见 §1 ⚠ 条） | 重新生成 PAT 并按 §1 写回字面 header；本机已不存在浏览器授权流程 |
| compose 工具报找不到文件 | server 容器无 Mac 文件系统 | 注入容器或宿主 CLI 操作（§2） |
| `claude mcp list` 探测 docker 报 name already in use | 固定名 `--name tny-mcp-docker` 的单实例副作用，会话容器在役时外部探测必失败 | 属预期，勿当故障：以会话内真实工具调用为准（如 container_list） |
| 容器 Up（`docker ps` 可见 `tny-mcp-docker`），但本会话启动时 spawn 撞名退出、会话内无工具；`claude mcp list` 假报 CONNECTION_CLOSED | 另一个开着但未用 docker 的旧会话在启动时抢占了单实例槽位（空占：容器内没数据） | ① `pgrep -f 'compose.*run.*tny-mcp'` 沿父链找到持有它的 claude 会话 PID；② `docker logs --since 24h tny-mcp-docker` 零输出（只有启动横幅）确认确属空占；③ 经用户确认后 `docker rm -f tny-mcp-docker` 回收——持有会话不受影响，其后续工具调用会自动重建；④ 回收后由需要工具的会话 `/mcp` 重连或重启会话去占槽，**外部探针不得抢槽**（固定名须留给会话自身的 spawn） |
| 槽位空闲（无 `tny-mcp-docker` 容器、固定名无人持有）但 `/mcp` 重连后仍显示 Failed | 重连的 spawn 没成功；先用注册命令原样手动复跑一次性握手（stdin 喂 initialize/tools/list，`--rm` 自清理不留槽）区分接线故障与宿主加载故障 | 手动复跑能返回 tools/list → 接线健康，问题在 Claude Code 本会话的连接层 → 重启会话（`claude --continue`）让启动时重新 spawn；手动复跑报错 → 按报错处置（镜像缺失/daemon 不通/环境 PATH） |
