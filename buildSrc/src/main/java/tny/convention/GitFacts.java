/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * git 命令输出到业务事实的解析与派生纯函数集合（convert-orchestration-to-java 任务 4.2，
 * 自 GitFlow/GitCli 消费面拆出；gradle-build-style"二进制实现的检查逻辑必须携带单元测试"
 * 需求的套用对象，红绿用例见 GitFactsTest）。
 *
 * <p>分工沿既有定型（consolidate-git-queries-into-gitflow）：通道与语义分居——本类只做
 * "输入命令输出行与事实值、输出派生事实"，进程执行留在 GitCli，编排与记忆化留在 GitFlow 与
 * 门禁接线；ls-remote 输出的守卫解析全仓唯一位置即本类 {@link #parseLsRemoteLines}
 * （上一变更数组越界崩溃的教训固化于此，调用点不再各自持有解析）。
 *
 * <p>正则匹配方向逐点依本册判例表（apply-notes）：`==~` 对应 matches 全串、`=~` 对应 find
 * 部分匹配；num 闭包必须 find（'dev/5.7.x' 带前缀名全串不可达，误用 matches 令数值比较
 * 静默 -1——e2e 第五轮实锤事故注记随迁）。
 */
public final class GitFacts {

    static final String RELEASE_VERSION_SUFFIX = ".release";
    static final String SNAPSHOT_PACK_SUFFIX = "-SNAPSHOT";
    static final String DEV_BRANCH_PREFIX = "dev/";
    static final String RELEASE_BRANCH_PREFIX = "release/";

    private static final Pattern BARE_TRIPLE = Pattern.compile("\\d+\\.\\d+\\.\\d+");
    private static final Pattern SERIES_ZERO = Pattern.compile("\\d+\\.\\d+\\.0$");
    private static final Pattern PLAIN_RELEASE_TAG = Pattern.compile("v\\d+\\.\\d+\\.\\d+");
    private static final Pattern NUM_TAIL = Pattern.compile("(\\d+)\\.(\\d+)\\.x$");
    private static final Pattern MARKER = Pattern.compile("\\b[A-Z][A-Z0-9]*(?:-[0-9]+)+\\b");

    private GitFacts() {
    }

    /** 裸三段号形态（adopt-plain-ga-versioning 规范出处）。 */
    public static boolean isBareTripleVersion(String value) {
        return value != null && BARE_TRIPLE.matcher(value).matches();
    }

    /** main 切维护分支仅用于系列首发 <N.M>.0。 */
    public static boolean isSeriesStartVersion(String value) {
        return value != null && SERIES_ZERO.matcher(value).matches();
    }

    /** describe 结果严格二次过滤：只认裸号 v<N.M.P>（旧代 -RELEASE 与 rc 形态拒绝，实测教训注记）。 */
    public static String describeTagOrNull(String describeOutput) {
        if (describeOutput == null) {
            return null;
        }
        String tag = describeOutput.trim();
        return PLAIN_RELEASE_TAG.matcher(tag).matches() ? tag : null;
    }

    /** 分支名到版本基的派生（redesign D2 语义逐字）。 */
    public static String parseBranchVersion(String version) {
        if (version.startsWith(DEV_BRANCH_PREFIX) || version.startsWith(RELEASE_BRANCH_PREFIX)) {
            return version.substring(version.indexOf('/') + 1);
        }
        if (isReleaseVersion(version)) {
            return version.substring(0, version.length() - RELEASE_VERSION_SUFFIX.length());
        }
        return version;
    }

    public static boolean isReleaseVersion(String version) {
        return version.endsWith(RELEASE_VERSION_SUFFIX);
    }

    /** 项目版本派生（dev 得 N.M.x-SNAPSHOT、release 维护分支须合法注入值且前段一致否则 null、
     * 旧一次性分支得裸三段号、其余线加 -SNAPSHOT），语义逐字承 GitFlow.parseProjectVersion。 */
    public static String parseProjectVersion(String branchName, String releaseVersion) {
        if (branchName.startsWith(DEV_BRANCH_PREFIX)) {
            return parseBranchVersion(branchName) + SNAPSHOT_PACK_SUFFIX;
        }
        if (branchName.startsWith(RELEASE_BRANCH_PREFIX)) {
            String base = parseBranchVersion(branchName);
            if (releaseVersion == null || !isBareTripleVersion(releaseVersion)) {
                return null;
            }
            return releaseVersion.startsWith(base.substring(0, base.lastIndexOf('.') + 1))
                    ? releaseVersion : null;
        }
        if (isReleaseVersion(branchName)) {
            return parseBranchVersion(branchName);
        }
        return branchName + SNAPSHOT_PACK_SUFFIX;
    }

    /** 分支类型派生（兼容访问器 gitBranchType 的判定段）。 */
    public static String branchType(String branchName) {
        if (branchName.startsWith(DEV_BRANCH_PREFIX) || branchName.startsWith(RELEASE_BRANCH_PREFIX)) {
            return "version";
        }
        if (branchName.toLowerCase(Locale.ROOT).endsWith(".release")) {
            return "release";
        }
        if (branchName.toLowerCase(Locale.ROOT).endsWith(".x")) {
            return "dev";
        }
        return "test";
    }

    /** 编号键：主*1000+次（find 语义——带前缀分支名必须部分匹配，事故注记在册）；不可解析为 -1。 */
    public static int seriesKey(String branch) {
        Matcher matcher = NUM_TAIL.matcher(branch);
        return matcher.find()
                ? Integer.parseInt(matcher.group(1)) * 1000 + Integer.parseInt(matcher.group(2))
                : -1;
    }

    /** porcelain v1 脏项映射：仅跟踪变更（?? 丢弃），长度不足 4 的行丢弃，重命名行保留"旧 -> 新"原文。 */
    public static List<String> porcelainTrackedPaths(List<String> porcelainLines) {
        List<String> paths = new ArrayList<>();
        for (String line : porcelainLines) {
            if (line.length() > 3 && !line.startsWith("??")) {
                paths.add(line.substring(3));
            }
        }
        return paths;
    }

    /** ls-remote 输出唯一守卫解析：按空白切两段取"引用名→提交号"，不足两段的行丢弃。 */
    public static Map<String, String> parseLsRemoteLines(List<String> lines) {
        Map<String, String> refs = new LinkedHashMap<>();
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length > 1) {
                refs.put(parts[1], parts[0]);
            }
        }
        return refs;
    }

    /** 系列已发布补丁号集合：只认 ^{} 解引用行（轻量标签无该行，天然不参与计数）。 */
    public static List<Integer> releasedPatchesFromRefs(Iterable<String> refNames, String base) {
        Pattern pattern = Pattern.compile(
                "^refs/tags/v" + Pattern.quote(base) + "\\.(\\d+)\\^\\{\\}$");
        List<Integer> patches = new ArrayList<>();
        for (String refName : refNames) {
            Matcher matcher = pattern.matcher(refName);
            if (matcher.matches()) {
                patches.add(Integer.parseInt(matcher.group(1)));
            }
        }
        return patches;
    }

    /** 下一补丁号期望：无标签时首发 .0，否则最大补丁加一。 */
    public static int nextPatchExpected(List<Integer> patches) {
        return patches.isEmpty() ? 0 : patches.stream().mapToInt(Integer::intValue).max().getAsInt() + 1;
    }

    /** 被搬运提交说明中的缺陷编号提取（大写字母-数字全串，声明序去重）。 */
    public static List<String> extractMarkers(List<String> logLines) {
        Set<String> found = new LinkedHashSet<>();
        for (String line : logLines) {
            Matcher matcher = MARKER.matcher(line);
            while (matcher.find()) {
                found.add(matcher.group());
            }
        }
        return new ArrayList<>(found);
    }

    /** git grep -c 输出行的命中数求和（取末段冒号后数字，非数字行丢弃——原形态逐字）。 */
    public static long sumGrepHits(List<String> grepLines) {
        long sum = 0;
        for (String line : grepLines) {
            String[] parts = line.split(":");
            String tail = parts[parts.length - 1];
            if (tail.matches("\\d+")) {
                sum += Long.parseLong(tail);
            }
        }
        return sum;
    }

    /** 祖父登记表解析：注释行与空行丢弃，"分支名|身份|依据" 竖线分段去空白，身份取第二段。 */
    public static Map<String, String> parseLegacyRegistry(List<String> registryLines) {
        Map<String, String> identityByBranch = new LinkedHashMap<>();
        for (String line : registryLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] parts = trimmed.split("\\|");
            if (parts.length >= 2) {
                identityByBranch.put(parts[0].trim(), parts[1].trim());
            }
        }
        return identityByBranch;
    }

    /** 提交时刻格式化（旧式 new Date(epoch*1000).format("yyyyMMdd_HHmm") 的 java.time 等价；
     * 时区口径由调用方传入 JVM 默认时区，保持"提交时刻换算到 JVM 默认时区"既有语义）。 */
    public static String formatCommitTime(long epochSeconds, ZoneId zone) {
        return DateTimeFormatter.ofPattern("yyyyMMdd_HHmm", Locale.ROOT)
                .withZone(zone)
                .format(Instant.ofEpochSecond(epochSeconds));
    }
}
