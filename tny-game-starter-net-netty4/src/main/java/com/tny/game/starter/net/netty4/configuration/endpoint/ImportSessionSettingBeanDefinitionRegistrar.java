package com.tny.game.starter.net.netty4.configuration.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.starter.common.initiator.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.starter.common.initiator.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportSessionSettingBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    public ImportSessionSettingBeanDefinitionRegistrar() {
        super(SESSION_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String settingClassName = this.environment.getProperty(key(keyHead, SETTING_CLASS_NODE), CommonSessionSetting.class.getName());
        Class<SessionSetting> settingClass = as(ExeAide.callUnchecked(() -> Class.forName(settingClassName)).orElse(null));
        String settingName = getBeanName(name, SessionSetting.class);
        registry.registerBeanDefinition(settingName, BeanDefinitionBuilder.genericBeanDefinition(settingClass,
                () -> Binder.get(this.environment)
                            .bind(keyHead, settingClass)
                            .orElseGet(() -> ExeAide.callUnchecked(settingClass::newInstance).orElse(null)))
                                                                          .addPropertyValue("name", name)
                                                                          .getBeanDefinition());
    }

}
