package com.tny.game.suite.cluster;


import com.tny.game.common.lifecycle.ServerPostStart;
import com.tny.game.suite.utils.Configs;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.tny.game.suite.utils.Configs.*;

public abstract class WebServiceCluster extends ServiceCluster implements ServerPostStart {

    @Resource
    private ServletContext servletContext;

    public WebServiceCluster(String serverType, boolean watchSetting, String... monitorWebTypes) {
        this(serverType, watchSetting, Arrays.asList(monitorWebTypes));
    }

    public WebServiceCluster(String serverType, boolean watchSetting, Collection<String> monitorWebTypes) {
        super(serverType, watchSetting, monitorWebTypes);
    }

    @Override
    protected String[] clusterUrls() {
        String part = this.servletContext.getContextPath();
        Map<String, String> urls = SERVICE_CONFIG.find(Configs.SERVER_URL + ".*");
        return urls.values().stream()
                .map(url -> {
                    if (url.startsWith("http") || url.startsWith("https"))
                        return url + part;
                    return url;
                })
                .toArray(String[]::new);
    }


}
