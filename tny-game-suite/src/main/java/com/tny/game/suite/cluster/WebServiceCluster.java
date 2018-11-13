package com.tny.game.suite.cluster;


import com.google.common.collect.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.suite.utils.*;

import javax.annotation.*;
import javax.servlet.*;
import java.util.*;

import static com.tny.game.suite.utils.Configs.*;

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
