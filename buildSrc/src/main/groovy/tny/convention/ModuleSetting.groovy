// 模块横切角色声明（consolidate-module-modes-plugin D1）：应用 tny.module-setting 的工程经本扩展的
// enableXxx 方法声明自身角色；角色存放在本工程扩展内，消费方沿依赖边核对（必要时 evaluationDependsOn
// 拉起被核对工程评估），不存在全局登记清单与跨文件字符串键（取代 tnyDemoBlueprints/tnyUnpublishedProjects）。
package tny.convention

import org.gradle.api.GradleException
import org.gradle.api.Project

class ModuleSetting {

    enum Mode {
        // 可作为 integrationTest 受控隔离子进程启动的应用蓝本
        APP,
        // 本工程不发布（零发布合同成员，benchmark-harness 情形泛化）
        UNPUBLISHED
    }

    private final Project project
    private final Set<Mode> modes = EnumSet.noneOf(Mode)

    ModuleSetting(Project project) {
        this.project = project
    }

    /** 本工程是应用形态蓝本（消费方：tny.demo-isolation 的受控隔离撮合）。 */
    void enableApp() {
        modes.add(Mode.APP)
    }

    /** 本工程不发布：声明即自检"不得属于发布线成员"，并禁用本工程 publish 任务（语义承接原 tny.unpublished）。 */
    void enableUnpublished() {
        modes.add(Mode.UNPUBLISHED)
        def javaProjects = project.rootProject.ext.has('javaProjects') ? project.rootProject.ext.javaProjects : []
        if (javaProjects.contains(project)) {
            throw new GradleException("零发布合同违例：'${project.path}' 声明 enableUnpublished()，却属于发布线 javaProjects 成员" +
                    "（命名后缀排除见 settings.gradle 约定注释）")
        }
        project.tasks.matching { it.name.toLowerCase().startsWith('publish') }.configureEach {
            enabled = false
        }
    }

    boolean has(Mode mode) {
        return modes.contains(mode)
    }

    /** 安全查询：未应用 tny.module-setting 的工程返回 false；要求 target 已完成评估（消费方沿边核对时负责评估到位）。 */
    static boolean enabled(Project target, Mode mode) {
        if (!target.state.executed) {
            throw new GradleException("ModuleSetting.enabled: 工程 '${target.path}' 尚未完成评估，查询方须先 evaluationDependsOn")
        }
        def declared = target.extensions.findByType(ModuleSetting)
        return declared != null && declared.has(mode)
    }
}
