package com.tny.game.common.context;

public class ContextAttributes extends AbstractAttributes {

	private static final Attributes EMPTY_ONE = new EmptyAttributes();

	public static Attributes empty() {
		return EMPTY_ONE;
	}

	public static Attributes create() {
		return new ContextAttributes();
	}

	public static Attributes create(AttrEntry<?>... entries) {
		return new ContextAttributes(entries);
	}

	private ContextAttributes(AttrEntry<?>... entries) {
		super(false);
		this.setAttribute(entries);
	}

}
