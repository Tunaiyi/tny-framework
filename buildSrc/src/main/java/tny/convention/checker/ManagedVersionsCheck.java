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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention.checker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 托管版本面对账守卫的判定逻辑（fix-dependency-version-governance 设计决策 D1；判定语义与
 * 守卫坐标表逐字迁移自原预编译脚本，pilot-binary-build-conventions 任务 5.1；需求规格见
 * gradle-build-style"托管版本面在配置期与声明事实源对账"）。
 *
 * <p>大头版本键声明的族，其代表坐标在托管合并模型中的生效版本必须等于事实源声明值；
 * 代表坐标从托管面消失（生效值取不到）同样计为失配。核对范围限于"成员版本随所属 BOM
 * 版本同值"的族（覆盖次序契约见 tny.dependency-management 块头注释）；
 * spring-boot-dependencies 的成员版本与键值不同值（例如 spring-core 为 6.1.2 而键为 3.2.1），
 * 不入本对账。
 *
 * <p>本类是纯输入输出判定：托管生效值快照（工程路径到坐标版本映射）与声明值由接线类
 * ModuleCheckerPlugin 在 projectsEvaluated 时机采集；configure-on-demand 下"仅核对本次已
 * 评估且带托管扩展的工程"的过滤也在接线侧完成。
 */
public final class ManagedVersionsCheck {

    /** 守卫坐标表（族版本键到代表坐标清单），与原脚本 map 逐项一致。 */
    public static final Map<String, List<String>> GUARD_COORDS;

    static {
        Map<String, List<String>> coords = new LinkedHashMap<>();
        coords.put("log4j2Version", List.of("org.apache.logging.log4j:log4j-api", "org.apache.logging.log4j:log4j-core"));
        coords.put("nettyVersion", List.of("io.netty:netty-common", "io.netty:netty-buffer", "io.netty:netty-transport"));
        coords.put("jacksonVersion", List.of("com.fasterxml.jackson.core:jackson-databind", "com.fasterxml.jackson.core:jackson-core"));
        coords.put("protobufVersion", List.of("com.google.protobuf:protobuf-java"));
        coords.put("slf4jVersion", List.of("org.slf4j:slf4j-api", "org.slf4j:slf4j-simple"));
        coords.put("testcontainersVersion", List.of("org.testcontainers:junit-jupiter", "org.testcontainers:mongodb"));
        coords.put("grpcVersion", List.of("io.grpc:grpc-core", "io.grpc:grpc-netty"));
        coords.put("alibabaCloudVersion", List.of("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery"));
        GUARD_COORDS = Map.copyOf(coords);
    }

    /** 单个工程的托管生效值快照视图。 */
    public record ProjectManagedView(String path, Map<String, String> managedVersions) {
    }

    private ManagedVersionsCheck() {
    }

    /** 逐族逐坐标核对并返回失配清单（空清单即对账通过）；declaredValues 为版本键到事实源声明值的映射。 */
    public static List<String> drifts(Map<String, String> declaredValues, List<ProjectManagedView> views) {
        List<String> drifts = new ArrayList<>();
        for (Map.Entry<String, List<String>> family : GUARD_COORDS.entrySet()) {
            String versionKey = family.getKey();
            String declared = declaredValues.get(versionKey);
            for (ProjectManagedView view : views) {
                for (String coord : family.getValue()) {
                    String effective = view.managedVersions().get(coord);
                    if (effective == null || !effective.equals(declared)) {
                        String shown = effective == null ? "（该族无托管源）" : "'" + effective + "'";
                        drifts.add(view.path() + " 的族 '" + versionKey + "' 失配：坐标 " + coord
                                + " 事实源声明 '" + declared + "' 实际生效 " + shown);
                    }
                }
            }
        }
        return drifts;
    }

    /** 聚合抛错文案（超 12 条截断展示），与原脚本 GradleException 消息逐字一致。 */
    public static String aggregate(List<String> drifts) {
        List<String> head = drifts.subList(0, Math.min(12, drifts.size()));
        return "托管版本面与声明事实源对账失败（修正声明或导入次序后重试；次序契约见 tny.dependency-management 的 imports 块头注释）：\n- "
                + String.join("\n- ", head)
                + (drifts.size() > 12 ? "\n- ……共 " + drifts.size() + " 条，仅列前 12 条" : "");
    }
}
