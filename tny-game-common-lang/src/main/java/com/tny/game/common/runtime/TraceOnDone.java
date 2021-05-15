package com.tny.game.common.runtime;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/11 9:52 上午
 */
@FunctionalInterface
public interface TraceOnDone {

    void onDone(ProcessTracer motion);

}
