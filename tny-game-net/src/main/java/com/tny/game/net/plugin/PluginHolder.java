package com.tny.game.net.plugin;

/**
 * 插件管理器
 *
 * @author KGTny
 * @ClassName: PluginHolder
 * @Description:
 * @date 2011-8-16 下午1:25:57
 * <p>
 * <p>
 * 持有所有控制器插件<br>
 */
public interface PluginHolder {

    /**
     * 获取某类型的插件
     * <p>
     * <p>
     * 当插件已经生成过,返回已生成插件,若无生产的插件,创建一个并添加到持有的Map<br>
     *
     * @param clazz 获取的插件类
     * @return 返回获取的插件对象
     * @throws InstantiationException 如果此 Class 表示一个抽象类、接口、数组类、基本类型或 void； 或者该类没有 null 构造方法；<br>
     *                                或者由于其他某种原因导致实例化失败。<br>
     * @throws IllegalAccessException 如果该类或其 null 构造方法是不可访问的。<br>
     */
    public <T extends ControllerPlugin> T getPlugin(Class<T> clazz);

}
