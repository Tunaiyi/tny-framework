/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb.utils;

import com.mongodb.*;
import org.slf4j.*;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 04:17
 */
public class MongoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUtils.class);

    private static final int DUPLICATE_KEY_ERROR = 11000;

    public static final String ID = "_id";

    public static boolean isIDNullValue(Map<String, ?> map) {
        Object id = map.get(ID);
        if (id != null) {
            return false;
        }
        return map.containsKey(ID);
    }

    public static boolean checkDuplicateKey(Throwable e) {
        Throwable throwable = e;
        if (throwable instanceof org.springframework.dao.DuplicateKeyException) {
            throwable = e.getCause();
        }
        if (throwable instanceof DuplicateKeyException) {
            LOGGER.warn("checkDuplicateKey DuplicateKeyException | {}", throwable.getMessage());
            return true;
        }
        if (throwable instanceof MongoWriteException) {
            LOGGER.warn("checkDuplicateKey MongoWriteException | {}", throwable.getMessage());
            MongoWriteException writeException = as(throwable);
            return writeException.getError().getCode() == DUPLICATE_KEY_ERROR;
        }
        return false;
    }

}
