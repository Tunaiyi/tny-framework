package com.tny.game.suite.cluster;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.lifecycle.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.*;

public abstract class WebServiceCluster extends ServiceCluster implements AppPostStart {

    @Resource
    private ServletContext servletContext;

    public WebServiceCluster(String serverType, boolean watchSetting, String... monitorWebTypes) {
        super(serverType, watchSetting, false, Arrays.asList(monitorWebTypes));
    }

    public WebServiceCluster(String serverType, boolean watchSetting, Collection<String> monitorWebTypes) {
        super(serverType, watchSetting, false, monitorWebTypes);
    }

    public WebServiceCluster(String serverType, boolean watchSetting, boolean monitorAllServices) {
        super(serverType, watchSetting, monitorAllServices, ImmutableList.of());
    }

    @Override
    protected String[] clusterUrls() {
        //        String part = this.servletContext.getContextPath();
        //        Map<String, String> urls = SERVICE_CONFIG.find(Configs.SERVER_URL + ".*");
        //        return urls.values().stream()
        //                .map(url -> {
        //                    if (url.startsWith("http") || url.startsWith("https")) {
        //                        return url + part;
        //                    }
        //                    return url;
        //                })
        //                .toArray(String[]::new);
        return new String[0];
    }

}
