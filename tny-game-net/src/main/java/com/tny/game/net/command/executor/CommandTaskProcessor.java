package com.tny.game.net.command.executor;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.endpoint.task.*;

/**
 * @author KGTny
 */
@UnitInterface
public interface CommandTaskProcessor {

    /**
     * 立即调度
     *
     * @param box 调度事件箱
     */
    void submit(CommandTaskBox box);

}
