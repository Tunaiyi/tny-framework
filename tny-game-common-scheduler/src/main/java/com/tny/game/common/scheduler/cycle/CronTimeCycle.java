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
