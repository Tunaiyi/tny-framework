package com.tny.game.suite.initer;

import com.tny.game.base.item.probability.RandomCreatorFactory;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.SubOfClassFilter;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Created by Kun Yang on 16/9/9.
 */
public class RandomCreatorIniter {

    public static ClassSelector selector() {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(RandomCreatorFactory.class))
                .setHandler(RandomCreatorIniter::handle);
    }

    private static void handle(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                continue;
            try {
                RandomCreatorFactory factory = (RandomCreatorFactory) clazz.newInstance();
                factory.registerSelf();
            } catch (Exception e) {
                ExceptionUtils.throwByCause(IllegalArgumentException::new, e, "创建 {} 异常", clazz);
            }
        }
    }

}
