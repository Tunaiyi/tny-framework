package com.tny.game.actor.local;

import com.tny.game.actor.exception.ConfigurationException;
import com.tny.game.actor.system.Setting;
import com.tny.game.common.config.Config;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Dispatchers
 *
 * @author KGTny
 */
public class Postmen {

    private Setting setting;

    private PostmanPrerequisites prerequisites;

    private Config config;

    private Map<String, PostmanConfigurator> configuratorMap = new HashMap<>();

    private static MessagePostman defaultGlobalPostman;

    private Set<String> idSet = new HashSet<>();

    public Postmen(Setting setting, PostmanPrerequisites prerequisites) {
        this.setting = setting;
        this.prerequisites = prerequisites;
        this.config = setting.getConfig().child("postman");
        registerConfig(this.config);
        defaultGlobalPostman = loadOrCreatePostmanConfigurator("default-postman").postman();
    }

    public MessagePostman getDefaultGlobalPostmen() {
        return defaultGlobalPostman;
    }

    public MessagePostman lookUp(String id) {
        return lookupConfigurator(id).postman();
    }

    private PostmanConfigurator lookupConfigurator(String id) {
        if (configuratorMap.containsKey(id)) {
            throw new ConfigurationException("Postmen [{}] 没有找到对应配置");
        } else {
            return configuratorMap.get(id);
        }
    }

    private void scanIDs(String key) {
        String keyText = StringUtils.remove(key, config.parentHeadKey());
        String[] keyTextWorlds = StringUtils.split(keyText, ".");
        if (keyTextWorlds.length > 0)
            idSet.add(keyTextWorlds[0]);
    }

    private void createPostmanConfigurator(String id) {
        loadOrCreatePostmanConfigurator(id);
    }

    private PostmanConfigurator loadOrCreatePostmanConfigurator(String id) {
        PostmanConfigurator configurator = configuratorMap.get(id);
        if (!configuratorMap.containsKey(id)) {
            Config postmanConfig = this.config.child(id);
            configurator = new PostmanConfigurator(id, postmanConfig, prerequisites);
            configuratorMap.put(id, configurator);
        }
        return configurator;
    }

    private void registerConfig(Config config) {
        config.keySet().stream().forEach(this::scanIDs);
        this.idSet.stream().forEach(this::createPostmanConfigurator);
    }

}
