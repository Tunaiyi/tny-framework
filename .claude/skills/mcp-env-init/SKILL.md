---
name: mcp-env-init
description: 初始化/重建/排障本机的 MCP 环境（GitHub MCP、Docker MCP、Docker skills 插件、docker mcp gateway）。当用户说「初始化 MCP」「重建 MCP 环境」「MCP 连不上」「换机器重装」或需要引用本机 MCP 接线方式时使用。
---

# MCP 环境初始化（本机实测配方）

2026-10-01 在本机（macOS arm64 / OrbStack / 无 gh CLI / git clone 受网络限制）完整验证过的搭建与排障流程。
所有步骤按序执行；带 ⚠ 的是本机特有约束，别按通用文档照抄。

## 架构总览（谁在跑）

| 组件 | 形态 | 宿主入口 |
|---|---|---|
| github-mcp-server v1.12.2 | stdio 容器（会话存续期常驻，`--rm` 会话结束即删） | `claude mcp get github` |
| docker-mcp (L337-org 2.2.6) | stdio 容器 + docker.sock 挂载，165 工具 | `claude mcp get docker` |
| docker-skills 插件 v0.3.1 | Claude Code plugin（user scope） | `claude plugin list` |
| docker mcp gateway v0.44.1 | 宿主 CLI 插件进程（stdio，profile tny，当前挂 docker-docs remote） | `claude mcp get docker-gateway` |
| 项目 compose 栈 | `docker/docker-compose.yaml`（etcd/redis/mongo, project=tny-framework-dev）；`docker/docker-compose.mcp.yaml`（MCP stdio 双服务声明，宿主 compose run 消费） | 见仓库根 |

**关键原则**：Claude Code 会话启动后才 `claude mcp add` 的 server，本会话不加载，需重启会话验证。

## 1. GitHub MCP — stdio + OAuth（唯一可行接线）

现网形态（2026-10-01 起统一为 compose 声明式，配置来源 = `docker/` 目录下的 `docker-compose.mcp.yaml` 的 `mcp-github-stdio` 服务，stdio profile）：

```bash
claude mcp add github -s user -- \
  docker compose -f <repo>/docker/docker-compose.mcp.yaml --profile stdio run --rm -T --name tny-mcp-github mcp-github-stdio
# 固定名必须用 run --name：compose 的 run 忽略服务级 container_name（实测 v5.5.1）

- 首次调用返回授权 URL → 浏览器登录授权 → 回调 localhost:8085 进容器 → 重试调用即通。token 仅内存：重启电脑/Docker 后每会话重新授权一次。
- ⚠ 为什么不用远程端点（`https://api.githubcopilot.com/mcp/`）：GitHub OAuth 服务器不支持 DCR（动态客户端注册），Claude Code 拿不到 client_id，报 `Incompatible auth server: does not support dynamic client registration`。
- ⚠ 为什么不用常驻 http 模式：实测 401 元数据链的 `authorization_servers` 指向 GitHub 本体 → 同样 DCR 死路（只适合自备 PAT 或前置 OAuth 代理的场景）。因此 compose 中曾有过的 `mcp-github`(serve/:8082) 常驻服务**已评估移除**（2026-10-01，定义见 git 历史）；出现多客户端共享端点需求时按上述条件恢复。
- 端口提醒：8085 若被其它项目 dev 容器占用，换端口需同时改 `-p` 与 `GITHUB_OAUTH_CALLBACK_PORT`（回调 URI 以 localhost:PORT 注册，端口必须两边一致）。

## 2. Docker MCP — stdio 直连 + socket 挂载

现网形态（compose 声明式，来源 = `docker/docker-compose.mcp.yaml` 的 `mcp-docker` 服务）：

```bash
claude mcp add docker -s user -- \
  docker compose -f <repo>/docker/docker-compose.mcp.yaml --profile stdio run --rm -T --name tny-mcp-docker mcp-docker
```
固定名 `tny-mcp-docker` / `tny-mcp-github` 便于 docker exec/logs 定位；**代价是单实例**——多会话并行或 `claude mcp list` 健康探测二次 spawn 会撞名（github 侧本就受 8085 端口单实例约束）。stdio 管道由宿主 spawn 持有，`up` 不拉 stdio 服务（profile 隔离的缘由）。

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
# docker/docker-compose.mcp.yaml 无 up 型服务：stdio 双服务由宿主 compose run 按需拉起（见 §1/§2）
```
详见两文件头注释（参数均对齐 IT 代码与 CI，勿随意改动 command/端口）。

## 6. 全链路验证清单

```bash
claude mcp list                          # 全绿 Connected
# stdio 握手探针（github/docker server 通用模板）：
( printf '%s\n' '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"p","version":"0"}}}' '{"jsonrpc":"2.0","method":"notifications/initialized"}' '{"jsonrpc":"2.0","id":2,"method":"tools/list"}'; sleep 6 ) \
  | docker run -i --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/l337-org/docker-mcp-server:2.2.6 2>/dev/null | tail -1 | head -c 200
nc -z localhost 2379 && nc -z localhost 6379          # 项目 compose 栈
./gradlew :tny-game-namnspace-etcd:test -q            # 2379 单测依赖路径
```

## 7. 故障对照表

| 症状 | 根因 | 处置 |
|---|---|---|
| `Incompatible auth server: does not support DCR` | 远程端点/http 模式 + GitHub 无 DCR | 改 stdio 接线（§1） |
| gateway 报 `Docker Desktop is not running` | Desktop 特性检查 | `DOCKER_MCP_IN_CONTAINER=1` |
| gateway 报 `Cannot connect to the Docker daemon at unix:///var/run/docker.sock` | 不读 docker context | `DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock` |
| gateway 报 `host path "/var/run/docker.sock" is blocked (sensitive system path)` | 安全策略硬禁 | 容器管理类改用 §2 直连 |
| `secrets engine is not available` | 非 Desktop 无 secrets store | 仅影响需 secret 的 server；env 兜底 |
| `claude plugin marketplace add <owner>/<repo>` 克隆失败 | 本机 git 通道受限 | curl tar → 本地路径注册（§3） |
| MCP 配置成功但会话里没有工具 | 会话早于配置启动 | 重启 `claude` 或 `--continue` |
| 重启电脑后 GitHub 调用 401/要求授权 | token 仅内存（设计如此） | 首次调用走一遍浏览器授权 |
| compose 工具报找不到文件 | server 容器无 Mac 文件系统 | 注入容器或宿主 CLI 操作（§2） |
| `claude mcp list` 里 github 报 CONNECTION_CLOSED 且 `docker ps` 无 github 容器 | 会话的 stdio 管道断过（容器已随 `--rm` 消失），传输惰性重启前的正常表象 | 直接调一次工具：Claude Code 会按需重起容器；新容器内存 token 为空 → 走一遍浏览器授权即恢复。独立健康探针请用别的宿主端口（如 `-p 127.0.0.1:18085:8085`），避免与在役会话容器抢 8085 造成假失败 |
| `claude mcp list` 探测 github/docker 报 name already in use（或端口冲突） | 固定名 `--name tny-mcp-*` + 8085 端口的单实例副作用，会话容器在役时外部探测必失败 | 属预期，勿当故障：以会话内真实工具调用为准（如 get_me / container_list） |
