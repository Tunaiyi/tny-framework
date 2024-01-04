/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.boot.registrar;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.*;

public class UnitLoadInitiator implements AppPrepareStart {

    @Autowired
    private ApplicationContext context;

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecyclePriorities.lower(LifecycleLevel.SYSTEM_LEVEL_MAX));
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