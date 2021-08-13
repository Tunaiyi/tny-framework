package com.tny.game.protoex.field;

/**
 * Map键值对编码方式配置
 *
 * @param <M>
 * @author KGTny
 */
public interface MapFieldOptions<M> extends FieldOptions<M> {

	/**
	 * key编码方式
	 *
	 * @return
	 */
	public FieldOptions<?> getKeyOptions();

	/**
	 * value编码方式
	 *
	 * @return
	 */
	public FieldOptions<?> getValueOptions();

}
