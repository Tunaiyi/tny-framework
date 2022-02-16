package com.tny.game.net.command.processor;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/12 2:37 AM
 */
public interface CommandTaskBoxDriverExecutor<T extends CommandTaskBoxDriver> {

	void execute(T driver);

	void schedule(T driver);

	int getBusSpinTimes();

	int getYieldTimes();

}
