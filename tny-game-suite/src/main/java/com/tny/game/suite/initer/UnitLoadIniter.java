package com.tny.game.suite.initer;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.common.unit.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class UnitLoadIniter implements AppPrepareStart {

    @Resource
    private ApplicationContext context;

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        Map<String, Object> unitInterfaces = this.context.getBeansWithAnnotation(UnitInterface.class);
        unitInterfaces.forEach((k, unitObject) -> {
            Unit unit = unitObject.getClass().getAnnotation(Unit.class);
            if (unit != null) {
                Set<String> names = UnitLoader.register(unitObject);
                if (!names.contains(k))
                    UnitLoader.register(k, unitObject);
            } else {
                UnitLoader.register(k, unitObject);
            }
        });
    }

}