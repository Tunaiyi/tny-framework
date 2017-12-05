package com.tny.game.common.config;

import com.tny.game.suite.base.Logs;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class SystemPropertiesLoader extends FileAlterationListenerAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(Logs.LOADER);

    public SystemPropertiesLoader(List<String> fileList) throws IOException {
        for (String file : fileList)
            roadProperties(file, true);
    }

    public SystemPropertiesLoader(String file) throws IOException {
        roadProperties(file, true);
    }

    private static void roadProperties(final String file, boolean listen)
            throws IOException {
        InputStream inputStream = null;
        try {
            if (listen) {
                inputStream = ConfigLoader.loadInputStream(file,
                        new PropertiesFileListener(file));
            } else {
                inputStream = ConfigLoader.loadInputStream(file);
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    private static class PropertiesFileListener extends
            FileAlterationListenerAdaptor {

        private String path;

        public PropertiesFileListener(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onFileChange(File file) {
            try {
                LOG.info("SystemProperties#读取配置系统属性配置{}文件......", path);
                roadProperties(path, false);
                LOG.info("SystemProperties#读取配置系统属性配置{}文件完成", path);
            } catch (IOException e) {
                LOG.error("SystemProperties#读取配置系统属性配置{}文件异常", path, e);
            }
        }
    }

}
