package com.tny.game.net.command.task;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/16 11:32 上午
 */
public enum CommandTaskBoxStatus {

    /* executor停止 */
    STOP(CommandTaskBoxConstants.STOP_VALUE),

    /* executor提交 */
    SUBMIT(CommandTaskBoxConstants.SUBMIT_VALUE),

    /* executor执行 */
    PROCESSING(CommandTaskBoxConstants.PROCESSING_VALUE),

    /* executor未完成延迟 */
    DELAY(CommandTaskBoxConstants.DELAY_VALUE),

    //
    ;

    private final int id;

    CommandTaskBoxStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
