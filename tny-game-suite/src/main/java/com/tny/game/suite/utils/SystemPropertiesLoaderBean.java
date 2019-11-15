package com.tny.game.suite.utils;

import com.tny.game.common.config.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;

import java.io.IOException;
import java.util.List;

public class SystemPropertiesLoaderBean implements BeanFactoryPostProcessor {

    private List<String> fileList;

    public SystemPropertiesLoaderBean(List<String> fileList) {
        this.fileList = fileList;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            @SuppressWarnings("unused")
            SystemPropertiesLoader systemPropertiesLoader = new SystemPropertiesLoader(this.fileList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
