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

package com.tny.game.common.scheduler.cycle;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * Created by Kun Yang on 16/2/20.
 */
public class CronTimeCycle implements TimeCycle {

    private CronExpression expression;

    private CronTimeCycle(CronExpression expression) {
        this.expression = expression;
    }

    public static final CronTimeCycle of(String expr) throws ParseException {
        return new CronTimeCycle(new CronExpression(expr));
    }

    public static final CronTimeCycle of(CronExpression expr) throws ParseException {
        return new CronTimeCycle(expr);
    }

    public CronExpression getExpression() {
        return this.expression;
    }

    @Override
    public Instant getTimeAfter(Instant instant) {
        Date after = this.expression.getTimeAfter(new Date(instant.toEpochMilli()));
        if (after == null) {
            // 无未来触发点（如固定年份已过）：显式异常交由上层按方案隔离，原实现裸 NPE 连坐整条调度链
            throw new IllegalStateException("cron 表达式 [" + this.expression.getCronExpression() + "] 无未来触发时间");
        }
        return Instant.ofEpochMilli(after.getTime());
    }

}
