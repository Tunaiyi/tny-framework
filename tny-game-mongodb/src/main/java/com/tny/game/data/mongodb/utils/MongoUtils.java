/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
