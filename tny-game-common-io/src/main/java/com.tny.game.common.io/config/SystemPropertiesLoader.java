/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.io.config;

import com.tny.game.common.utils.*;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class SystemPropertiesLoader extends FileAlterationListenerAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOADER);

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
                inputStream = FileIOAide.openInputStream(file,
                        new PropertiesFileListener(file));
            } else {
                inputStream = FileIOAide.openInputStream(file);
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
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
                LOG.info("SystemProperties#读取配置系统属性配置{}文件......", this.path);
                roadProperties(this.path, false);
                LOG.info("SystemProperties#读取配置系统属性配置{}文件完成", this.path);
            } catch (IOException e) {
                LOG.error("SystemProperties#读取配置系统属性配置{}文件异常", this.path, e);
            }
        }

    }

}
