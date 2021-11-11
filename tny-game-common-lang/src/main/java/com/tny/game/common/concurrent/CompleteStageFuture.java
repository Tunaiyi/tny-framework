package com.tny.game.common.concurrent;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2018/8/21.
 */
public class CompleteStageFuture<T> extends CompletableFuture<T> implements StageFuture<T> {

	public static <T> CompleteStageFuture<T> future(Throwable cause) {
		CompleteStageFuture<T> future = new CompleteStageFuture<>();
		future.completeExceptionally(cause);
		return future;
	}

	public static <T> CompleteStageFuture<T> success(T value) {
		CompleteStageFuture<T> future = new CompleteStageFuture<>();
		future.complete(value);
		return future;
	}

	public static <T> CompleteStageFuture<T> future() {
		return new CompleteStageFuture<>();
	}

}
