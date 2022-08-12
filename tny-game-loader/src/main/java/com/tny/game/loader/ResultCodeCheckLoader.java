/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.loader;

import com.tny.game.common.result.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:41 下午
 */
public class ResultCodeCheckLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResultCodeCheckLoader.class);

    @ClassSelectorProvider
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & ResultCode> ClassSelector ResultCodeClassesSelector() {
        return ClassSelector.create()
                .addFilter(SubOfClassFilter.ofInclude(ResultCode.class))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<E> enumList = EnumUtils.getEnumList((Class<E>)codeClass);
                        enumList.forEach(ResultCodes::registerCode);
                    }
                    LOGGER.info("ResultCodeCheckLoader : {}", codeClass);
                }));
    }

}
