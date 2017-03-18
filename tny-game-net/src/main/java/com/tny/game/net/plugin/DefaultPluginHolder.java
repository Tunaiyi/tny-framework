package com.tny.game.net.plugin;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
public class DefaultPluginHolder implements PluginHolder {

    /**
     * 插件Map
     */
    private ConcurrentMap<Class<? extends ControllerPlugin>, ControllerPlugin> pluginMap =
            new ConcurrentHashMap<Class<? extends ControllerPlugin>, ControllerPlugin>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ControllerPlugin> T getPlugin(Class<T> clazz) {
        final ControllerPlugin plugin = pluginMap.get(clazz);
        if (plugin != null)
            return (T) plugin;
        T newPlugin;
        try {
            newPlugin = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final ControllerPlugin oldPlugin = pluginMap.putIfAbsent(clazz, newPlugin);
        return (T) (oldPlugin == null ? newPlugin : oldPlugin);
    }

}
