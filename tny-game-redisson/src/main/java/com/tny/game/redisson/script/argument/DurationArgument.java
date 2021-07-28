package com.tny.game.redisson.script.argument;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * 时间参数
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/27 6:36 下午
 */
public class DurationArgument<B> extends ScriptArgument<B> {

    private Long time;

    public DurationArgument(B builder) {
        super(builder);
    }

    public DurationArgument(B builder, Long time) {
        super(builder);
        this.time = time;
    }

    public B withTime(long time) {
        this.time = time;
        return and();
    }

    public B withTime(long time, TimeUnit unit) {
        this.time = unit.toMillis(time);
        return and();
    }

    public long getTime() {
        return this.time;
    }

    public long getTime(long defaultTime) {
        return this.time == null ? defaultTime : this.time;
    }

    public long getTime(LongSupplier supplier) {
        return this.time == null ? supplier.getAsLong() : this.time;
    }

    public boolean hasValue() {
        return this.time != null;
    }

}
