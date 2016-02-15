package com.tny.game.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应的未来对象
 * @author KGTny
 *
 * @param <V>
 */
public class FailedAnswer<V> extends Answer<V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FailedAnswer.class);

	private Throwable cause;

	public static <V> Answer<V> failed() {
		return new FailedAnswer<>();
	}

	public static <V> Answer<V> failed(Throwable cause) {
		return new FailedAnswer<>(cause);
	}

	private FailedAnswer() {
		this(null);
	}

	private FailedAnswer(Throwable cause) {
		super();
		this.cause = cause;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void addListener(AnswerListener<V> listener) {
		try {
			listener.answerWith(this, null, cause);
		} catch (Exception e) {
			LOGGER.error("{}.answerWith  ", listener.getClass(), e);
		}
	}

	@Override
	public V result() {
		return null;
	}

}
