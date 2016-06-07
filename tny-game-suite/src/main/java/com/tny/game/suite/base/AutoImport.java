package com.tny.game.suite.base;

import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class AutoImport {

    protected static void loadClass(String key) {
        try {
            String classesName = Configs.SUITE_CONFIG.getStr(key, "");
            if (StringUtils.isNotBlank(classesName)) {
                String[] classNames = StringUtils.split(classesName, ",");
                for (String className : classNames)
                    Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
