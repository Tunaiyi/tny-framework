package com.tny.game.suite.initer;

import com.tny.game.base.item.Ability;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.common.enums.Enums;
import com.tny.game.common.result.ResultCode;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.SubOfClassFilter;
import com.tny.game.suite.base.capacity.CapacitySupplierType;
import com.tny.game.net.base.ScopeType;
import com.tny.game.net.base.AppType;

/**
 * Created by Kun Yang on 16/9/9.
 */
public class EnumLoader {

    public static ClassSelector selector() {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(
                        ResultCode.class,
                        Ability.class,
                        ScopeType.class,
                        AppType.class,
                        ItemType.class,
                        Action.class,
                        Behavior.class,
                        DemandType.class,
                        DemandParam.class,
                        Module.class,
                        Feature.class,
                        OpenMode.class,
                        CapacitySupplierType.class))
                .setHandler((classes) -> classes.stream()
                        .filter(Class::isEnum)
                        .map(c -> (Class<? extends Enum>) c)
                        .forEach(Enums::getEnumList)
                );
    }

}
