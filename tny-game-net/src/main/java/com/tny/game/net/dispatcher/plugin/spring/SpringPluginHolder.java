package com.tny.game.net.dispatcher.plugin.spring;

import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author KGTny
 * @ClassName: SpringPluginHolder
 * @Description: 通过Spring管理PluginHolder
 * @date 2011-10-11 下午3:08:07
 * <p>
 * 通过Spring管理PluginHolder
 * <p>
 * 通过Spring管理PluginHolder<br>
 */
public class SpringPluginHolder implements PluginHolder, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 插件Map
     */
    private ConcurrentMap<Class<? extends ControllerPlugin>, ControllerPlugin> pluginMap =
            new ConcurrentHashMap<Class<? extends ControllerPlugin>, ControllerPlugin>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ControllerPlugin> T getPlugin(Class<T> clazz) {
        T plugin = (T) this.pluginMap.get(clazz);
        if (plugin == null) {
            plugin = this.applicationContext.getBean(clazz);
            if (plugin == null)
                return null;
            this.pluginMap.put(clazz, plugin);
        }
        return plugin;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, ControllerPlugin> springPluginMap = applicationContext.getBeansOfType(ControllerPlugin.class);
        if (springPluginMap == null)
            return;
        for (ControllerPlugin plugin : springPluginMap.values())
            this.pluginMap.put(plugin.getClass(), plugin);
    }

}
