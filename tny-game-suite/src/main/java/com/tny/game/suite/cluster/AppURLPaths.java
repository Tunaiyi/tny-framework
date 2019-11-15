package com.tny.game.suite.cluster;


import com.google.common.base.MoreObjects;
import com.tny.game.common.utils.*;
import com.tny.game.suite.utils.*;

/**
 * Created by Kun Yang on 16/8/11.
 */
public class AppURLPaths {

    public static final String HTTP_INSIDE = "http-inside";

    public static final String HTTP_OUTSIDE = "http-outside";

    public static AppURLPath path(String appType, String key, String protocol, String defaultPath) {
        return new DefaultAppURLPath(appType, key, protocol, defaultPath);
    }

    private static class DefaultAppURLPath implements AppURLPath {

        private String name;
        private String protocol;
        private String path;
        private String appType;

        private DefaultAppURLPath(String serverType, String name, String protocol, String defaultPath) {
            this.name = name;
            this.protocol = protocol;
            this.appType = serverType;
            String key = Configs.PATH_HEAD + serverType + "." + name;
            this.path = Configs.PROTOCOLS_CONFIG.getStr(key, defaultPath);
            Throws.checkNotNull(this.path, "{} path is null", key);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAppType() {
            return appType;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getProtocol() {
            return protocol;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("name", name)
                              .add("path", path)
                              .toString();
        }
    }
}
