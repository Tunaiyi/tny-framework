package com.tny.game.suite.cluster;


import com.google.common.base.MoreObjects;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.suite.utils.Configs;

/**
 * Created by Kun Yang on 16/8/11.
 */
public class WebPaths {

    public static final String HTTP_INSIDE = "http-inside";

    public static final String HTTP_OUTSIDE = "http-outside";

    public static WebPath path(String serverType, String key, String defaultPath) {
        return new DefaultWebPath(serverType, key, defaultPath);
    }

    private static class DefaultWebPath implements WebPath {

        private String name;
        private String path;
        private String serverType;

        private DefaultWebPath(String serverType, String name, String defaultPath) {
            this.name = name;
            this.serverType = serverType;
            String key = Configs.PATH_HEAD + serverType + "." + name;
            this.path = Configs.PROTOCOLS_CONFIG.getStr(key, defaultPath);
            ExceptionUtils.checkNotNull(this.path, "{} path is null", key);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getServerType() {
            return null;
        }

        @Override
        public String getPath() {
            return path;
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
