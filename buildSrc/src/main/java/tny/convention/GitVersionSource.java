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

/**
 * 派生版本供给契约（convert-orchestration-to-java 任务 5.1）：根工程 gitFlow 扩展向
 * {@link ProjectsPlugin} 注入点暴露的最小类型化面——注入点据此摆脱装配线册登记遗留的
 * {@code GroovyObject.getProperty} 弱型反射（该册 design D4 的编译墙降级，本册
 * {@link GitFlow} 转 Java 后按本契约转正）。实现方为 GitFlow；测试替身实现同一契约。
 */
public interface GitVersionSource {

    /** 按分支形态与 -PreleaseVersion 注入值派生的工程版本；未注入且为 release 维护分支时为 null。 */
    String getProjectVersion();
}
