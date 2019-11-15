package com.tny.game.suite.net.configuration.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportTerminalSettingBeanDefinitionRegistrar extends SuiteBeanDefinitionRegistrar {

    public ImportTerminalSettingBeanDefinitionRegistrar() {
        super(TERMINAL_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String settingClassName = environment.getProperty(key(keyHead, SETTING_CLASS_NODE), CommonTerminalSetting.class.getName());
        Class<TerminalSetting> settingClass = as(ExeAide.callUnchecked(() -> Class.forName(settingClassName)).orElse(null));
        String settingName = getBeanName(name, TerminalSetting.class);
        registry.registerBeanDefinition(settingName, BeanDefinitionBuilder.genericBeanDefinition(settingClass,
                () -> Binder.get(environment)
                            .bind(keyHead, settingClass)
                            .orElseGet(() -> ExeAide.callUnchecked(settingClass::newInstance).orElse(null)))
                                                                          .addPropertyValue("name", name)
                                                                          .getBeanDefinition());
    }

}
