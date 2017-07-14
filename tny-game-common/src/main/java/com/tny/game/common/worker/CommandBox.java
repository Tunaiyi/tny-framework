package com.tny.game.common.worker;

import com.tny.game.common.worker.command.Command;


/**
 * 命令箱
 *
 * @param <C>
 */

public interface CommandBox<C extends Command> extends CommandWorker {

    /**
     * @return box命令列表是否为空
     */
    boolean isEmpty();

    /**
     * @return 命令数量
     */
    int size();

    /**
     * 清除命令
     */
    void clear();

    /**
     * 接受命令
     *
     * @param command 命令
     * @return 是否接收成功
     */
    boolean accept(C command);

    /**
     * 绑定worker
     *
     * @param worker worker
     * @return 返回是否绑定成功
     */
    boolean bindWorker(CommandWorker worker);

    /**
     * @return 是否解绑worker成功
     */
    boolean unbindWorker();

    /**
     * @return 提交给worker
     */
    boolean submit();

    /**
     * 处理Command
     */
    void process();

}
