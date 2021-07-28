package com.tny.game.boot.initiator;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.*;

public class UnitLoadInitiator implements AppPrepareStart {

    @Resource
    private ApplicationContext context;

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_MAX);
    }

    @Override
    public void prepareStart() {
        Map<String, Object> unitInterfaces = this.context.getBeansWithAnnotation(UnitInterface.class);
        unitInterfaces.forEach((k, unitObject) -> {
            Unit unit = unitObject.getClass().getAnnotation(Unit.class);
            if (unit != null) {
                Set<String> names = UnitLoader.register(unitObject);
                if (!names.contains(k)) {
                    UnitLoader.register(k, unitObject);
                }
            } else {
                UnitLoader.register(k, unitObject);
            }
        });
    }

}