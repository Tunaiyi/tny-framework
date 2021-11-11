package com.tny.game.common.concurrent;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * Created by Kun Yang on 2018/8/21.
 */
public class WrapperStageFuture<T> implements StageFuture<T> {

	protected CompletableFuture<T> future;

	public WrapperStageFuture(CompletableFuture<T> future) {
		this.future = future;
	}

	protected CompletableFuture<T> future() {
		return future;
	}

	@Override
	public boolean isCancelled() {
		return future().isCancelled();
	}

	@Override
	public boolean isDone() {
		return future().isDone();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future().cancel(mayInterruptIfRunning);
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return future().get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future().get(timeout, unit);
	}

	@Override
	public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
		return future().thenApply(fn);
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		return new WrapperStageFuture<>(future().thenApplyAsync(fn));
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		return new WrapperStageFuture<>(future().thenApplyAsync(fn, executor));
	}

	@Override
	public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
		return new WrapperStageFuture<>(future().thenAccept(action));
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
		return new WrapperStageFuture<>(future().thenAcceptAsync(action));
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		return new WrapperStageFuture<>(future().thenAcceptAsync(action, executor));
	}

	@Override
	public CompletionStage<Void> thenRun(Runnable action) {
		return new WrapperStageFuture<>(future().thenRun(action));
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action) {
		return new WrapperStageFuture<>(future().thenRunAsync(action));
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
		return new WrapperStageFuture<>(future().thenRunAsync(action, executor));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		return new WrapperStageFuture<>(future().thenCombine(other, fn));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		return new WrapperStageFuture<>(future().thenCombineAsync(other, fn));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn,
			Executor executor) {
		return new WrapperStageFuture<>(future().thenCombineAsync(other, fn, executor));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		return new WrapperStageFuture<>(future().thenAcceptBoth(other, action));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		return new WrapperStageFuture<>(future().thenAcceptBothAsync(other, action));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action,
			Executor executor) {
		return new WrapperStageFuture<>(future().thenAcceptBothAsync(other, action, executor));
	}

	@Override
	public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		return new WrapperStageFuture<>(future().runAfterBoth(other, action));
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return new WrapperStageFuture<>(future().runAfterBothAsync(other, action));
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return new WrapperStageFuture<>(future().runAfterBothAsync(other, action));
	}

	@Override
	public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return new WrapperStageFuture<>(future().applyToEither(other, fn));
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return new WrapperStageFuture<>(future().applyToEitherAsync(other, fn));
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
		return new WrapperStageFuture<>(future().applyToEitherAsync(other, fn, executor));
	}

	@Override
	public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return new WrapperStageFuture<>(future().acceptEither(other, action));
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return new WrapperStageFuture<>(future().acceptEitherAsync(other, action));
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
		return new WrapperStageFuture<>(future().acceptEitherAsync(other, action));
	}

	@Override
	public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		return new WrapperStageFuture<>(future().runAfterEither(other, action));
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return new WrapperStageFuture<>(future().runAfterEitherAsync(other, action));
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return new WrapperStageFuture<>(future().runAfterEitherAsync(other, action, executor));
	}

	@Override
	public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		return new WrapperStageFuture<>(future().thenCompose(fn));
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return new WrapperStageFuture<>(future().thenComposeAsync(fn));
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
		return new WrapperStageFuture<>(future().thenComposeAsync(fn, executor));
	}

	@Override
	public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return new WrapperStageFuture<>(future().exceptionally(fn));
	}

	@Override
	public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return new WrapperStageFuture<>(future().whenComplete(action));
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return new WrapperStageFuture<>(future().whenCompleteAsync(action));
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		return new WrapperStageFuture<>(future().whenCompleteAsync(action, executor));
	}

	@Override
	public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		return new WrapperStageFuture<>(future().handle(fn));
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return new WrapperStageFuture<>(future().handleAsync(fn));
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		return new WrapperStageFuture<>(future().handleAsync(fn, executor));
	}

	protected void complete(T message) {
		if (!this.future.isDone()) {
			this.future.complete(message);
		}
	}

	protected void completeExceptionally(Throwable e) {
		if (!this.future.isDone()) {
			this.future.completeExceptionally(e);
		}
	}

	protected void cancel() {
		if (!this.future.isDone()) {
			this.future.cancel(true);
		}
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		throw new UnsupportedOperationException("unsupport toCompletableFuture");
	}

	public boolean await() throws InterruptedException {
		if (future.isDone()) {
			return !future.isCompletedExceptionally();
		}
		try {
			future.get();
			return true;
		} catch (ExecutionException e) {
			future.completeExceptionally(e);
		}
		return false;
	}

	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			future.get(timeout, unit);
			return true;
		} catch (ExecutionException | TimeoutException e) {
			future.completeExceptionally(e);
		}
		return false;
	}

	public boolean awaitUninterruptibly() {
		try {
			return await();
		} catch (InterruptedException e) {
			future.completeExceptionally(e);
		}
		return false;
	}

	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		try {
			return await(timeout, unit);
		} catch (InterruptedException e) {
			future.completeExceptionally(e);
		}
		return false;
	}

}
